package bugs.decentralized.model

import org.springframework.data.annotation.Id

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
    var waitTime: Long = 0L,
    var leaderboard: MutableList<Node> = mutableListOf()
) {

    override fun toString(): String {
        return "$address:$url"
    }

    fun compareLeaderboard(newLeaderboard: List<Node>): Boolean {
        for (i in leaderboard.indices) {
            if (leaderboard[i].address != newLeaderboard[i].address ||
                leaderboard[i].url != newLeaderboard[i].url
            )
                return false
        }
        return true
    }
}
