package bugs.decentralized.model

import org.springframework.data.annotation.Id

data class Node(
    @Id
    val id: String,
    val url: String,
) {

    override fun toString(): String {
        return "$id:$url"
    }
}
