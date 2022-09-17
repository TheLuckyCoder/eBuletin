package bugs.decentralized.blockchain

import bugs.decentralized.controller.NodesService
import bugs.decentralized.controller.Poet
import bugs.decentralized.controller.ValidatorController
import bugs.decentralized.model.Block
import bugs.decentralized.model.SimpleNode
import kotlinx.coroutines.*
import java.util.*

class Blockchain(
    private val blocks: MutableList<Block>,
    private val validatorController: ValidatorController,
    private val nodesService: NodesService
) {
    private var nodeList: MutableList<SimpleNode> = validatorController.nodes().toMutableList()

    private fun getActiveNodes(): MutableList<SimpleNode> {
        val activeNodes: MutableList<SimpleNode> = mutableListOf()

        for (node in nodeList) {
            if (nodesService.nodeIsAlive(node.url))
                activeNodes.add(node)
        }

        return activeNodes
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun miningSession(currentNode: SimpleNode) {
        /**
         * Pe parcursul [Poet.WAIT_TIME] -> asteapta + genereaza leaderboard
         * Pe parcursul [Poet.VOTING_TIME] -> trimite + primeste blockuri propuse
         * Dupa [Poet.VOTING_TIME] se adauga block-ul pe blockchain
         **/
        currentNode.nonce = Poet.computeNonce(blocks.last(), currentNode.address)
        val leaderboard = Poet.computeLeaderboard(getActiveNodes(), blocks.last())

            GlobalScope.launch {
                delay(Poet.WAIT_TIME)
                currentNode.block = Poet.generateBlock(validatorController.transactions(), blocks, currentNode)
                //TODO: Broadcast the block


                GlobalScope.launch {
                    //TODO receive blocks
                    //TODO update leaderboard
                }

                delay(Poet.VOTING_TIME)
                for(node in leaderboard){
                    if(node.block != GENESIS_BLOCK){
                        //TODO add block to the blockchain
                        blocks.add(node.block)
                        return@launch
                    }
            }
        }
    }

    //TODO make verify() useful
    private fun verify() {
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

    companion object {
        val GENESIS_BLOCK = Block(0L, System.currentTimeMillis(), emptyList(), "", "")
    }
}
