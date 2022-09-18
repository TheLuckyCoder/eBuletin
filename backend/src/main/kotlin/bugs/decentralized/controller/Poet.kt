package bugs.decentralized.controller

import bugs.decentralized.model.AccountAddress
import bugs.decentralized.model.Block
import bugs.decentralized.model.Node
import bugs.decentralized.model.Transaction
import bugs.decentralized.utils.SHA
import java.math.BigInteger
import kotlin.random.Random

object Poet {
    //TODO: find values:
    private const val MIN_TIME = 30_000L //ms -> 30 s
    private const val MAX_TIME = 120_000L //ms -> 2 min

    fun computeWaitTime(lastBlock: Block, nodeAddress: String): Long {
        val hash = SHA.sha256Hex(lastBlock.computeHash() + nodeAddress)
        val seed = BigInteger(hash, 16)
        val rand = Random(seed.toLong())
        return rand.nextLong(MIN_TIME, MAX_TIME)
    }

    fun generateBlock(transactions: List<Transaction>, blocks: List<Block>, currentNode: Node): Block {
        /** Create a new block which will "point" to the last block. **/
        val lastBlock = blocks.last()
        return Block(
            lastBlock.blockNumber + 1,
            System.currentTimeMillis(),
            transactions,
            lastBlock.computeHash(),
            currentNode.address,
        )
    }
}
