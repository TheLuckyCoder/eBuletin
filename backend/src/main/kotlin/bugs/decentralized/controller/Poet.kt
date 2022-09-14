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
    private const val EPSILON = 20L //ms
    private const val MIN_TIME = 1_000L //ms -> 1 s
    private const val MAX_TIME = 60_000L //ms -> 1 min

    /** Returns a list of Nodes sorted by [computeWaitTime] **/
    private fun computeLeaderboard(activeNodes: List<Node>, lastBlock: Block): List<Node> {
        //compute waitTimes
        for (node in activeNodes) {
            node.waitTime = computeWaitTime(lastBlock, node.address)
        }
        //sort the nodes by waitTime
        activeNodes.sortedBy { it.waitTime }
        return activeNodes
    }

    private fun computeWaitTime(lastBlock: Block, nodeAddress: String): Long {
        val hash = SHA.sha256Hex(lastBlock.hash + nodeAddress)
        val seed = BigInteger(hash, 16)
        val rand = Random(seed.toLong())
        return rand.nextLong(MIN_TIME, MAX_TIME)
    }

    fun generateBlock(transactions: List<Transaction>, blocks: List<Block>, currentNode: Node): Block {
        /** Create a new block which will "point" to the last block. **/
        val lastBlock = blocks.last()
        return Block(
            lastBlock.blockNumber + 1u,
            System.currentTimeMillis(),
            transactions,
            lastBlock.hash,
            currentNode.address
        )
    }

    fun assignLeaderboardToEachNode(activeNodes: List<Node>, lastBlock: Block) {
        /** Generates the leaderboard and assigns it ot each node **/
        val temList = computeLeaderboard(activeNodes, lastBlock).toMutableList()
        for (node in activeNodes) {
            node.leaderboard = temList
        }
    }

    fun initiateVotingRound(activeNodes: List<Node>): Node {
        /** Returns the node that is eligible to add the next block to the blockchain **/
        //the list of active nodes has
        for (node in activeNodes) {
            if (node.compareLeaderboard(activeNodes))
                return node
        }
        return activeNodes.last()
    }
}