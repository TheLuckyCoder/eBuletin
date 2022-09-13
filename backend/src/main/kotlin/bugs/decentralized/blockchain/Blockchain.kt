package bugs.decentralized.blockchain

import bugs.decentralized.controller.NodesService
import bugs.decentralized.controller.ValidatorController
import bugs.decentralized.model.Block
import bugs.decentralized.model.Node
import bugs.decentralized.model.Transaction
import bugs.decentralized.repository.BlockRepository
import bugs.decentralized.repository.NodesRepository
import bugs.decentralized.utils.SHA
import bugs.decentralized.utils.epsilonEquals
import java.math.BigInteger
import java.util.*
import kotlin.random.Random.Default.nextLong


class Blockchain(
    private val blocks: MutableList<Block>
) {

    private val nodesService: NodesService = TODO()
    private val validatorController: ValidatorController = TODO()
    private var waitTime: Long = 14400

    fun mineBlock(transactions: List<Transaction>): Block {
        // Create a new block which will "point" to the last block.
        val lastBlock = blocks.last()

        assignMineTimeForEachNode(validatorController.nodes())

        return if (isVotingRoundLegit(validatorController.nodes()))
            Block(lastBlock.blockNumber + 1u, System.currentTimeMillis(), transactions, lastBlock.hash, waitTime)
        else
            lastBlock
    }

    private fun isVotingRoundLegit(nodes: List<Node>): Boolean {
        var isValid = true
        for (node in nodes) {
            if (node.mineTime != nextLong(node.address.toLong() + blocks.last().hash.toLong()) ||
                !nodesService.nodeIsAlive(node.url)
            ) {
                isValid = false
                break
            }
        }

        return isValid
    }

    private fun assignMineTimeForEachNode(nodes: List<Node>) {
        for (node in nodes) {
            if (nodesService.nodeIsAlive(node.url))
                node.assignMiningTime(blocks.last().hash.toLong())
        }
    }

    fun verify() {
        check(blocks.isNotEmpty()) { "Blockchain can't be empty" }
        check(blocks[0] == GENESIS_BLOCK) { "Invalid first block!" }

        for (i in 1 until blocks.size) {
            val current = blocks[i]

            check(current.blockNumber == i.toULong()) { "Invalid block number ${current.blockNumber} for block #${i}!" }

            val previous = blocks[i - 1]
            check(current.parentHash == previous.hash) { "Invalid previous block hash for block #$i!" }

            /**cannot verify [expectedTime] against [waitingTime] (-> not stored anywhere)*/
            //check(isPoetValid(previous, )) { "Invalid waiting time for block #$i!" }
        }
    }

    fun submitTransaction(senderAddress: String, receiverAddress: String, data: String) {
        /*transactionPool.add(
            Transaction(
                sender = senderAddress,
                receiver = receiverAddress,
            )
        )*/
    }

    private fun isPoetValid(poet: Long, currentWaitingTime: Long): Boolean =
        poet in currentWaitingTime - 10L..currentWaitingTime + 10L

    companion object {
        val GENESIS_BLOCK = Block(0UL, System.currentTimeMillis(), emptyList(), "", 0L)
    }
}
