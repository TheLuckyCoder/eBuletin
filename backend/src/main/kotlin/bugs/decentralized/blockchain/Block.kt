package bugs.decentralized.blockchain

import bugs.decentralized.model.Transaction
import bugs.decentralized.utils.SHA
import org.springframework.data.annotation.Id

data class Block(
    @Id
    val blockNumber: ULong, // the length of the blockchain in blocks
    val timestamp: Long,
    val transactions: List<Transaction>,
    val parentHash: String,
    val nonce: Int = 0, // a hash that, when combined with the mixHash, proves that the block has gone through proof-of-work
) {

    private var _hash: String? = null
    val hash: String = _hash ?: computeHash().let {
        _hash = it
        it
    }

    private fun computeHash() =
        SHA.sha256(blockNumber.toString() + timestamp + transactions.joinToString("") { it.hash } + parentHash + nonce)
}
