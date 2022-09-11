package bugs.decentralized.model

import org.springframework.data.annotation.Id
import kotlin.random.Random.Default.nextLong

data class Node(
    @Id
    val address: String,
    val url: String,
    var isLeader: Boolean = false,
    var mineTime: ULong = 0UL
) {

    override fun toString(): String {
        return "$address:$url"
    }

    fun assignMiningTime() {
        if (!isLeader)
            mineTime = nextLong().toULong()
    }
}
