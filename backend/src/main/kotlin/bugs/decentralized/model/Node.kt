package bugs.decentralized.model

import kotlinx.serialization.Serializable
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Serializable
@Document
class Node(
    @Id
    val address: String, // AccountAddress
    val url: String
) {

    override fun toString(): String {
        return "$address:$url"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Node

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
