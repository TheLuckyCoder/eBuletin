package bugs.decentralized.blockchain

import bugs.decentralized.controller.NodesService
import bugs.decentralized.controller.Poet
import bugs.decentralized.controller.ValidatorController
import bugs.decentralized.model.Block
import bugs.decentralized.model.Node
import bugs.decentralized.model.Transaction
import java.util.*


class Blockchain(
    private val blocks: MutableList<Block>,
    private val validatorController: ValidatorController,
    private val nodesService: NodesService
) {
    private var nodeList: MutableList<Node> = TODO()

    /** Why do we use [SimpleNode] ?
     * I don't get it.
     * It's stupid
     **/

    private fun getActiveNodes(): MutableList<Node> {
        val activeNodes: MutableList<Node> = mutableListOf<Node>()

        for (node in nodeList) {
            if (nodesService.nodeIsAlive(node.url))
                activeNodes.add(node)
        }

        return activeNodes
    }

    private fun getTransactions(): List<Transaction> {
        //TODO
        return emptyList()
    }

    fun miningSession(currentNode: Node) {
        Poet.computeWaitTime(blocks.last(), currentNode.address)
        Thread.sleep(currentNode.waitTime)

        Poet.computeLeaderboard(getActiveNodes(), blocks.last())

        if (currentNode == currentNode.leaderboard[0])
            Poet.generateBlock(getTransactions(), blocks, currentNode)
    }

    fun verify() {
        check(blocks.isNotEmpty()) { "Blockchain can't be empty" }
        check(blocks[0] == GENESIS_BLOCK) { "Invalid first block!" }

        for (i in 1 until blocks.size) {
            val current = blocks[i]

            check(current.blockNumber == i.toLong()) { "Invalid block number ${current.blockNumber} for block #${i}!" }

            val previous = blocks[i - 1]
            check(current.parentHash == previous.getHash()) { "Invalid previous block hash for block #$i!" }

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

    companion object {
        val GENESIS_BLOCK = Block(0L, System.currentTimeMillis(), emptyList(), "", "")
    }
}
