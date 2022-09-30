package bugs.decentralized.blockchain

import bugs.decentralized.model.Block
import bugs.decentralized.model.Node
import bugs.decentralized.model.Transaction
import bugs.decentralized.utils.SHA
import kotlin.random.Random
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object Poet {
    private val MIN_TIME = 2.seconds
    private val MAX_TIME = 30.seconds//1.minutes
    val epsilon = 10.seconds

    fun computeWaitTime(lastBlock: Block, nodeAddress: String): Long {
        val hash = SHA.sha256Hex(lastBlock.hash + nodeAddress)
        //convert only the first 15 hex digits to avoid overflow
        val seed = hash.substring(0, 15).toLong(16)
        val rand = Random(seed)
        return rand.nextLong(MIN_TIME.inWholeMilliseconds, MAX_TIME.inWholeMilliseconds)
    }

    fun generateBlock(transactions: List<Transaction>, lastBlock: Block, currentNode: Node, generalHash: String): Block {
        /** Create a new block which will "point" to the last block. **/
        return Block(
            lastBlock.blockNumber + 1,
            System.currentTimeMillis(),
            transactions,
            lastBlock.hash,
            currentNode.address,
            generalHash
        )
    }
}
