package bugs.decentralized.model

import org.springframework.data.annotation.Id

data class Node(
    @Id
    val address: String,
    val url: String
) {

    override fun toString(): String {
        return "$address:$url"
    }

}
