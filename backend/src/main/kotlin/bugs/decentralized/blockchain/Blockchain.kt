package bugs.decentralized.blockchain

import bugs.decentralized.model.Block
import bugs.decentralized.model.Transaction
import java.math.BigInteger

class Blockchain(
    private val blocks: MutableList<Block>
) {

    fun mineBlock(transactions: List<Transaction>): Block {
        // Create a new block which will "point" to the last block.
        val lastBlock = blocks.last()
        var newBlock = Block(lastBlock.blockNumber + 1u, System.currentTimeMillis(), transactions, lastBlock.hash)

        while (true) {
            val pow = newBlock.hash
            println("Mining #${newBlock.blockNumber}: nonce: ${newBlock.nonce}, pow: $pow")

            if (isPoWValid(pow)) {
                println("Found valid POW: ${pow}!");
                break
            }

            newBlock = newBlock.copy(nonce = newBlock.nonce + 1)
        }

        return newBlock
    }

    fun verify() {
        check(blocks.isNotEmpty()) { "Blockchain can't be empty" }
        check(blocks[0] == GENESIS_BLOCK) { "Invalid first block!" }

        for (i in 1 until blocks.size) {
            val current = blocks[i]

            check(current.blockNumber == i.toULong()) { "Invalid block number ${current.blockNumber} for block #${i}!" }

            val previous = blocks[i - 1]
            check(current.parentHash == previous.hash) { "Invalid previous block hash for block #$i!" }

            check(isPoWValid(current.hash)) { "Invalid previous block hash's difficutly for block #$i!" }
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
        val GENESIS_BLOCK = Block(0UL, System.currentTimeMillis(), emptyList(), "", 0)
        const val DIFFICULTY = 2
        const val TARGET = 1 shl (256 - DIFFICULTY)

        fun isPoWValid(pow: String): Boolean {
            return try {
                val newPow = pow.removePrefix("0x")
                BigInteger(newPow, 16).compareTo(BigInteger.valueOf(TARGET.toLong())) != 1 // less then or equal
            } catch (e: NumberFormatException) {
                false
            }
        }
    }
}
