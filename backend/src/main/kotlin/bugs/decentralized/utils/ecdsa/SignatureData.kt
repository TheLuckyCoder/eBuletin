package bugs.decentralized.utils.ecdsa

import kotlinx.serialization.Serializable

@Serializable
class SignatureData(val v: ByteArray, val r: ByteArray, val s: ByteArray) {

    constructor(v: Byte, r: ByteArray, s: ByteArray) : this(byteArrayOf(v), r, s)

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val that = other as SignatureData
        if (!v.contentEquals(that.v)) {
            return false
        }
        return if (!r.contentEquals(that.r)) {
            false
        } else s.contentEquals(that.s)
    }

    override fun hashCode(): Int {
        var result = v.contentHashCode()
        result = 31 * result + r.contentHashCode()
        result = 31 * result + s.contentHashCode()
        return result
    }
}
