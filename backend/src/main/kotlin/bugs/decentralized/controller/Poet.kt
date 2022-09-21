package bugs.decentralized.controller

import bugs.decentralized.model.Block
import bugs.decentralized.model.Node
import bugs.decentralized.model.Transaction
import bugs.decentralized.utils.SHA
import java.math.BigInteger
import kotlin.random.Random
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object Poet {
    private val MIN_TIME = 10.seconds
    private val MAX_TIME = 1.minutes

    fun computeWaitTime(lastBlock: Block, nodeAddress: String): Long {
        val hash = SHA.sha256Hex(lastBlock.computeHash() + nodeAddress)
        val seed = BigInteger(hash, 16)
        val rand = Random(seed.toLong())
        return rand.nextLong(MIN_TIME.inWholeMilliseconds, MAX_TIME.inWholeMilliseconds)
    }

    fun generateBlock(transactions: List<Transaction>, lastBlock: Block, currentNode: Node): Block {
        /** Create a new block which will "point" to the last block. **/
        return Block(
            lastBlock.blockNumber + 1,
            System.currentTimeMillis(),
            transactions,
            lastBlock.hash,
            currentNode.address,
        )
    }
}
