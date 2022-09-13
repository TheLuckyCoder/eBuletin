package bugs.decentralized.model

import org.springframework.data.annotation.Id
import kotlin.random.Random.Default.nextLong

class SimpleNode(
    @Id
    val address: String,
    val url: String,
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
    var mineTime: Long = 0L
) {

    override fun toString(): String {
        return "$address:$url"
    }

    fun assignMiningTime(lastBlock: Long) {
        mineTime = nextLong(address.hashCode().toLong() + lastBlock.hashCode())
    }
}
