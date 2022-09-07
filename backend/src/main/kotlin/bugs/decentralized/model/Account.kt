package bugs.decentralized.model

import bugs.decentralized.utils.toHexString
import kotlinx.serialization.Serializable
import java.security.MessageDigest

@Serializable
@JvmInline
value class PublicAccountKey(val value: String) {
    fun toAddress(): AccountAddress {
        val hash = MessageDigest
            .getInstance("SHA-3")
            .digest(value.toByteArray())

        val hex = "0x" + hash.takeLast(20).toByteArray().toHexString()
        return AccountAddress(hex)
    }
}

@Serializable
@JvmInline
value class AccountAddress(val value: String) {

    init {
        require(value.length == 10)
    }
}
