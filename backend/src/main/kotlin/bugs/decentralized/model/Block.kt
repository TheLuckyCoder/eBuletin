package bugs.decentralized.model

import bugs.decentralized.utils.SHA
import kotlinx.serialization.Serializable
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.mapping.Document

@Serializable
@Document
data class Block(
    @field:Id
    val blockNumber: Long, // the length of the blockchain in blocks
    val timestamp: Long,
    val transactions: List<Transaction>,
    val parentHash: String,
    val nodeAddress: String
) {

    @kotlinx.serialization.Transient
    @Transient
    private var _hash: String? = null

    val hash: String
        get() = _hash ?: computeHash().let {
            _hash = it
            it
        }

    private fun computeHash() =
        SHA.sha256Hex(blockNumber.toString() + timestamp + transactions.joinToString("") { it.hash } + parentHash + nonce)
}
