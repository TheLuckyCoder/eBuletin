package bugs.decentralized.model

import bugs.decentralized.utils.SHA
import org.springframework.data.annotation.Id

data class Block(
    @Id
    val blockNumber: ULong, // the length of the blockchain in blocks
    val timestamp: Long,
    val transactions: List<Transaction>,
    val parentHash: String,
    val nonce: ULong = 0UL, // proves that the node has waited the necessary amount of time to create a new block
) {

    private var _hash: String? = null
    val hash: String = _hash ?: computeHash().let {
        _hash = it
        it
    }

    private fun computeHash() =
        SHA.sha256(blockNumber.toString() + timestamp + transactions.joinToString("") { it.hash } + parentHash + nonce)

}
