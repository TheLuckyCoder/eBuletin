package bugs.decentralized.blockchain

import bugs.decentralized.model.Block
import java.util.*


class Blockchain(
    private val blocks: MutableList<Block>
) {
    //TODO implement POET

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
        val GENESIS_BLOCK = Block(0UL, System.currentTimeMillis(), emptyList(), "", "")
    }
}
