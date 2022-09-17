package bugs.decentralized.model

import bugs.decentralized.blockchain.Blockchain
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class SimpleNode(
    @Id
    val address: String,
    val url: String,
    var nonce: Long = 0L,
    var block: Block = Blockchain.GENESIS_BLOCK
) {
    override fun toString(): String {
        return "$address:$url"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SimpleNode

        if (address != other.address) return false
        if (url != other.url) return false
        if (nonce != other.nonce) return false

        if (block != other.block)
            return false

        return true
    }

    override fun hashCode(): Int {
        var result = address.hashCode()
        result = 31 * result + url.hashCode()
        return result
    }
}

data class Node(
    @Id
    val address: String,
    val url: String,
    var waitTime: Long = 0L,
) {

    override fun toString(): String {
        return "$address:$url"
    }
}
