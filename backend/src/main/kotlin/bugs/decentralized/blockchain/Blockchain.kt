package bugs.decentralized.blockchain

import bugs.decentralized.controller.ValidatorController
import bugs.decentralized.model.Block
import bugs.decentralized.model.Node
import bugs.decentralized.model.Transaction
import bugs.decentralized.repository.BlockRepository
import bugs.decentralized.repository.NodesRepository

class Blockchain(
    private val blocks: MutableList<Block>
) {
    private val blockRepository: BlockRepository = TODO()
    private val nodesRepository: NodesRepository = TODO()
    /***Why doesn't it work
     * I don't get it
     * please help*/
    private val validatorController: ValidatorController = TODO()
    var waitTime: ULong = 0UL

    fun mineBlock(transactions: List<Transaction>): Block {
        // Create a new block which will "point" to the last block.
        val lastBlock = blocks.last()
        waitTime = ULong.MAX_VALUE

//        assignMineTimeForEachNode(validatorController.nodes())

        return Block(lastBlock.blockNumber + 1u, System.currentTimeMillis(), transactions, lastBlock.hash, waitTime)
    }

    private fun assignMineTimeForEachNode(nodes: List<Node>) {
        for (node in nodes) {
            node.assignMiningTime()

            if (node.mineTime < waitTime)
                waitTime = node.mineTime
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

            check(isPoetValid(current.nonce, waitTime)) { "Invalid waiting time for block #$i!" }
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

    companion object {
        val GENESIS_BLOCK = Block(0UL, System.currentTimeMillis(), emptyList(), "", 0UL)

        fun isPoetValid(poet: ULong, currentWaitingTime: ULong): Boolean {
            return currentWaitingTime - 1UL <= poet && currentWaitingTime + 1UL >= poet
        }
    }
}
