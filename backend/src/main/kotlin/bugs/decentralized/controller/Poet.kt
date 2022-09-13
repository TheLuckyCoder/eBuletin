package bugs.decentralized.controller

import bugs.decentralized.model.Block
import bugs.decentralized.model.Node
import bugs.decentralized.model.Transaction
import bugs.decentralized.utils.SHA
import bugs.decentralized.utils.epsilonEquals
import java.math.BigInteger
import java.util.*

class Poet {
    private val validatorController: ValidatorController = TODO()
    private val nodesService: NodesService = TODO()
    private val nodes = validatorController.nodes()
    private val blocks = validatorController.blocks()

    private fun isVotingRoundLegit(nodes: List<Node>): Boolean {
        var isValid = true
        for (node in nodes) {
            if (node.mineTime != kotlin.random.Random.nextLong(node.address.toLong() + blocks.last().hash.toLong()) ||
                !nodesService.nodeIsAlive(node.url)
            ) {
                isValid = false
                break
            }
        }

        return isValid
    }

    private fun assignMineTimeForEachNode() {
        for (node in nodes) {
            if (nodesService.nodeIsAlive(node.url))
                node.assignMiningTime(blocks.last().hash.toLong())
        }
    }

    fun isPoetValid(previousBlock: Block, nodeAddress: String, waitTime: Long): Boolean {
        val expectedTime = computeWaitTime(previousBlock, nodeAddress)
        return expectedTime.epsilonEquals(waitTime, EPSILON)
    }

    fun computeWaitTime(previousBlock: Block, nodeAddress: String): Long {
        val hash = SHA.sha256Hex(previousBlock.hash + nodeAddress)
        val seed = BigInteger(hash, 16)
        val rand = Random(seed.toLong())
        return rand.nextLong(MIN_TIME, MAX_TIME)
    }

    fun mineBlock(transactions: List<Transaction>, blocks: List<Block>, currentNode: Node): Block {
        // Create a new block which will "point" to the last block.
        val lastBlock = blocks.last()
        return Block(
            lastBlock.blockNumber + 1u,
            System.currentTimeMillis(),
            transactions,
            lastBlock.hash,
            currentNode.address
        )
    }

    companion object {
        //TODO: find values:
        private const val BLOCK_TIME = 60_000L //ms -> 1 min
        private const val EPSILON = 20L //ms
        private const val MIN_TIME = 1_000L //ms -> 1 s
        private const val MAX_TIME = 60_000L //ms -> 1 min

    }

}