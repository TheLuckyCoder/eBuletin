package bugs.decentralized.controller

import bugs.decentralized.model.Block
import bugs.decentralized.model.Node
import bugs.decentralized.model.Transaction
import bugs.decentralized.utils.SHA
import bugs.decentralized.utils.epsilonEquals
import java.math.BigInteger
import java.util.*

class Poet {

    fun isPoetValid(previousBlock: Block, nodeAddress: String, waitTime: Long): Boolean {
        val expectedTime = computeWaitTime(previousBlock, nodeAddress)
        return expectedTime.epsilonEquals(waitTime, EPSILON)
    }

    fun computeWaitTime(previousBlock: Block, nodeAddress: String): Long {
        val hash = SHA.sha256(previousBlock.hash + nodeAddress)
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