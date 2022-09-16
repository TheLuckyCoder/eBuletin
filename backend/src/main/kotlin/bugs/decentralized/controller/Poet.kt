package bugs.decentralized.controller

import bugs.decentralized.controller.Poet.BLOCK_TIME
import bugs.decentralized.controller.Poet.computeWaitTime
import bugs.decentralized.model.Block
import bugs.decentralized.model.Node
import bugs.decentralized.model.Transaction
import bugs.decentralized.utils.SHA
import java.math.BigInteger
import kotlin.random.Random

/**
 * 1. Every node computes its own [waitTime] and all other nodes' [waitTimes] using the [computeWaitTime] function
 * 2. The nodes wait for their computed [waitTime]
 * 3. Each node broadcasts its proposed [Block]
 * 4. After the [BLOCK_TIME] has passed the voting session starts
 * 5. Every node checks if the leader truly has the shortest [computeWaitTime]
 * 6. If a node is lying the next node is eligible to mine the block
 * 7. A new block is added to the blockchain
 * */

object Poet {
    //TODO: find values:
    private const val BLOCK_TIME = 60_000L //ms -> 1 min
    private const val MIN_TIME = 1_000L //ms -> 1 s
    private const val MAX_TIME = 60_000L //ms -> 1 min
    const val WAIT_TIME = 69_000L

    /** Returns a list of Nodes sorted by [computeWaitTime] **/
    fun computeLeaderboard(activeNodes: List<Node>, lastBlock: Block): List<Node> {
        //compute waitTimes
        for (node in activeNodes) {
            node.waitTime = computeWaitTime(lastBlock, node.address)
        }
        //sort the nodes by waitTime
        activeNodes.sortedBy { it.waitTime }
        return activeNodes
    }

    fun computeWaitTime(lastBlock: Block, nodeAddress: String): Long {
        val hash = SHA.sha256Hex(lastBlock.getHash() + nodeAddress)
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
            lastBlock.getHash(),
            currentNode.address
        )
    }
}