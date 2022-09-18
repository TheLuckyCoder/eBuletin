package bugs.decentralized.utils.ecdsa

import bugs.decentralized.model.PublicAccountKey
import org.bouncycastle.util.encoders.Hex
import java.lang.StringBuilder
import java.math.BigInteger

class SimpleKeyPair(private val private: ByteArray, private val public: ByteArray) {

    val privateBinary: ByteArray
        get() = private

    val publicBinary: ByteArray
        get() = public

    val privateHex: String
        get() = Hex.toHexString(private)

    val publicHex: String
        get() = Hex.toHexString(public)

    val privateInteger: BigInteger
        get() = privateHex.toBigInteger(16)

    val publicInteger: BigInteger
        get() = publicHex.toBigInteger(16)

    val publicAccount: PublicAccountKey
        get() = PublicAccountKey(publicHex)

    companion object {
        fun from(keyPair: Sign.ECKeyPair): SimpleKeyPair {
            val publicKeyHex = StringBuilder(Hex.toHexString(keyPair.publicKey.toByteArray()))
            if (publicKeyHex.length == 65 * 2) {
                publicKeyHex[0] = '0'
                publicKeyHex[1] = '4'
            } else { // 554942632bcb2d361207191a06953f3f2385f1c6e4fa79a881997419492f8cdd6e368ecb399f4d221db1257054c453ea201bdd731d574e058b30a0ba548b867a
                while (publicKeyHex.length < 65 * 2)
                    publicKeyHex.insert(0, '0')
                publicKeyHex[1] = '4'
            }

            return SimpleKeyPair(keyPair.privateKey.toByteArray(), Hex.decode(publicKeyHex.toString()))
        }
    }
}
