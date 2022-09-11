package bugs.decentralized.utils.ecdsa

import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.params.ECPrivateKeyParameters
import org.bouncycastle.crypto.signers.ECDSASigner
import org.bouncycastle.crypto.signers.HMacDSAKCalculator
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey
import java.math.BigInteger
import java.security.KeyPair

class ECKeyPair(val privateKey: BigInteger, val publicKey: BigInteger) {

    /**
     * Sign a hash with the private key of this key pair.
     *
     * @param transactionHash the hash to sign
     * @return An [ECDSASignature] of the hash
     */
    fun sign(transactionHash: ByteArray): ECDSASignature {
        val signer = ECDSASigner(HMacDSAKCalculator(SHA256Digest()))
        val privKey = ECPrivateKeyParameters(privateKey, Sign.CURVE)
        signer.init(true, privKey)
        val components: Array<BigInteger> = signer.generateSignature(transactionHash)
        return ECDSASignature(components[0], components[1]).toCanonicalised()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val ecKeyPair = other as ECKeyPair
        if (privateKey != ecKeyPair.privateKey) {
            return false
        }
        return publicKey == ecKeyPair.publicKey
    }

    override fun hashCode(): Int {
        var result = privateKey.hashCode()
        result = 31 * result + publicKey.hashCode()
        return result
    }

    companion object {
        fun create(keyPair: KeyPair): ECKeyPair {
            val privateKey = keyPair.private as BCECPrivateKey
            val publicKey = keyPair.public as BCECPublicKey
            val privateKeyValue = privateKey.d

            // Ethereum does not use encoded public keys like bitcoin - see
            // https://en.bitcoin.it/wiki/Elliptic_Curve_Digital_Signature_Algorithm for details
            // Additionally, as the first bit is a constant prefix (0x04) we ignore this value
            val publicKeyBytes: ByteArray = publicKey.q.getEncoded(false)
            val publicKeyValue = BigInteger(1, publicKeyBytes.copyOfRange(1, publicKeyBytes.size))
            return ECKeyPair(privateKeyValue, publicKeyValue)
        }

        fun create(privateKey: BigInteger): ECKeyPair {
            return ECKeyPair(privateKey, Sign.publicKeyFromPrivate(privateKey))
        }
    }
}
