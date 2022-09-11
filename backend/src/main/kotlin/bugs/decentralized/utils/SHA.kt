package bugs.decentralized.utils

import java.security.MessageDigest

object SHA {
    fun sha256(input: String) = hashString("SHA-256", input).toHexString()

    fun sha256Bytes(input: String) = hashString("SHA-256", input)

    private fun hashString(type: String, input: String): ByteArray {
        return MessageDigest
            .getInstance(type)
            .digest(input.toByteArray())
    }
}

fun ByteArray.toHexString(): String {
    return joinToString("") { "%02x".format(it) }
}

fun String.decodeHex(): ByteArray {
    check(length % 2 == 0) { "Must have an even length" }

    return ByteArray(length / 2) {
        Integer.parseInt(this, it * 2, (it + 1) * 2, 16).toByte()
    }
}
