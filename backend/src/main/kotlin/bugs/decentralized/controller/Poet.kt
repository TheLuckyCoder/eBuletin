package bugs.decentralized.controller

import bugs.decentralized.controller.Poet.computeNonce
import bugs.decentralized.model.Block
import bugs.decentralized.model.SimpleNode
import bugs.decentralized.model.Transaction
import bugs.decentralized.utils.SHA
import java.math.BigInteger
import kotlin.random.Random

object Poet {
    const val BLOCK_TIME = 60_000L
    const val WAIT_TIME = 50_000L
    const val VOTING_TIME = BLOCK_TIME - WAIT_TIME

    /** Returns a list of Nodes sorted by [computeNonce] **/
    fun computeLeaderboard(activeNodes: List<SimpleNode>, lastBlock: Block): List<SimpleNode> {
        //compute waitTimes
        for (node in activeNodes) {
            node.nonce = computeNonce(lastBlock, node.address)
        }
        //sort the nodes by waitTime
        activeNodes.sortedBy { it.nonce }
        return activeNodes
    }

    fun computeNonce(lastBlock: Block, nodeAddress: String): Long {
        val hash = SHA.sha256Hex(lastBlock.getHash() + nodeAddress)
        val seed = BigInteger(hash, 16)
        val rand = Random(seed.toLong())
        return rand.nextLong(Long.MIN_VALUE, Long.MAX_VALUE)
    }

    fun generateBlock(transactions: List<Transaction>, blocks: List<Block>, currentNode: SimpleNode): Block {
        /** Create a new block which will "point" to the last block. **/
        val lastBlock = blocks.last()
        return Block(
            lastBlock.blockNumber + 1,
            System.currentTimeMillis(),
            transactions,
            lastBlock.getHash(),
            currentNode.address
        )
    }
}