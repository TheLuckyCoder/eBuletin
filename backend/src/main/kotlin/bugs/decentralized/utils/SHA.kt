package bugs.decentralized.utils

import org.bouncycastle.util.encoders.Hex
import java.security.MessageDigest

object SHA {
    fun sha256Hex(input: String): String = Hex.toHexString(hashString("SHA-256", input))

    fun sha256Bytes(input: String) = hashString("SHA-256", input)

    private fun hashString(type: String, input: String): ByteArray {
        return MessageDigest
            .getInstance(type)
            .digest(input.toByteArray())
    }
}
