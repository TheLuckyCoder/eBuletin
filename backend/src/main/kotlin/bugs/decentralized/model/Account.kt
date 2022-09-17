package bugs.decentralized.model

import bugs.decentralized.utils.SHA
import kotlinx.serialization.Serializable
import org.bouncycastle.util.encoders.Hex

@Serializable
@JvmInline
value class PublicAccountKey(val value: String) {

    fun toAddress(): AccountAddress {
        val hex = "0x" + Hex.toHexString(SHA.sha256Bytes(value.removePrefix("04")).takeLast(20).toByteArray())
        return AccountAddress(hex)
    }
}

@Serializable
@JvmInline
value class AccountAddress(val value: String) {

    init {
        require(value.startsWith("0x")) { "Account Address must start with 0x ($value)" }
    }
}
