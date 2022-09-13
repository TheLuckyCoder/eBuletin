package bugs.decentralized.model

import bugs.decentralized.utils.SHA
import org.springframework.data.annotation.Id

data class Block(
    @Id
    val blockNumber: ULong, // the length of the blockchain in blocks
    val timestamp: Long,
    val transactions: List<Transaction>,
    val parentHash: String,
    val nodeAddress: String
) {

    private var _hash: String? = null
    val hash: String = _hash ?: computeHash().let {
        _hash = it
        it
    }

    private fun computeHash() =
        SHA.sha256Hex(blockNumber.toString() + timestamp + transactions.joinToString("") { it.hash } + parentHash)

}
