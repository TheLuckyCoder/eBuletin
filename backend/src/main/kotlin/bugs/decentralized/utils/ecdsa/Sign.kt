package bugs.decentralized.utils.ecdsa

import bugs.decentralized.utils.SHA
import org.bouncycastle.asn1.x9.X9ECParameters
import org.bouncycastle.asn1.x9.X9IntegerConverter
import org.bouncycastle.crypto.ec.CustomNamedCurves
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.math.ec.ECAlgorithms
import org.bouncycastle.math.ec.ECPoint
import org.bouncycastle.math.ec.FixedPointCombMultiplier
import org.bouncycastle.math.ec.custom.sec.SecP256K1Curve
import java.math.BigInteger
import java.security.SignatureException
import java.util.*


/**
 * https://github.com/web3j/web3j/blob/master/crypto/src/main/java/org/web3j/crypto/Sign.java
 */
object Sign {

    val CURVE_PARAMS: X9ECParameters = CustomNamedCurves.getByName("secp256k1")
    const val CHAIN_ID_INC = 35
    const val LOWER_REAL_V = 27

    // The v signature parameter starts at 37 because 1 is the first valid chainId so:
    // chainId >= 1 implies that 2 * chainId + CHAIN_ID_INC >= 37.
    // https://eips.ethereum.org/EIPS/eip-155
    const val REPLAY_PROTECTED_V_MIN = 37
    val CURVE = ECDomainParameters(
        CURVE_PARAMS.curve,
        CURVE_PARAMS.g,
        CURVE_PARAMS.n,
        CURVE_PARAMS.h
    )
    val HALF_CURVE_ORDER = CURVE_PARAMS.n.shiftRight(1)

    @Throws(SignatureException::class)
    fun signedMessageToKey(message: String, signatureData: SignatureData): BigInteger {
        return signedMessageHashToKey(SHA.sha256Bytes(message), signatureData)
    }

    @Throws(SignatureException::class)
    fun signedMessageHashToKey(messageHash: ByteArray, signatureData: SignatureData): BigInteger {
        require(signatureData.r.size == 32) { "r must be 32 bytes" }
        require(signatureData.s.size == 32) { "s must be 32 bytes" }
        val header: Int = signatureData.v[0].toInt() and 0xFF
        // The header byte: 0x1B = first key with even y, 0x1C = first key with odd y,
        //                  0x1D = second key with even y, 0x1E = second key with odd y
        if (header < 27 || header > 34) {
            throw SignatureException("Header byte out of range: $header")
        }
        val sig = ECDSASignature(
            BigInteger(1, signatureData.r),
            BigInteger(1, signatureData.s)
        )
        val recId = header - 27
        return recoverFromSignature(recId, sig, messageHash)
            ?: throw SignatureException("Could not recover public key from signature")
    }

    private fun recoverFromSignature(recId: Int, sig: ECDSASignature, message: ByteArray): BigInteger? {
        require(recId in 0..3) { "recId must be in the range of [0, 3]" }
        require(sig.r.signum() >= 0) { "r must be positive" }
        require(sig.s.signum() >= 0) { "s must be positive" }

        // 1.0 For j from 0 to h   (h == recId here and the loop is outside this function)
        //   1.1 Let x = r + jn
        val n = CURVE.n // Curve order.
        val i = BigInteger.valueOf(recId.toLong() / 2)
        val x = sig.r.add(i.multiply(n))
        //   1.2. Convert the integer x to an octet string X of length mlen using the conversion
        //        routine specified in Section 2.3.7, where mlen = ⌈(log2 p)/8⌉ or mlen = ⌈m/8⌉.
        //   1.3. Convert the octet string (16 set binary digits)||X to an elliptic curve point R
        //        using the conversion routine specified in Section 2.3.4. If this conversion
        //        routine outputs "invalid", then do another iteration of Step 1.
        //
        // More concisely, what these points mean is to use X as a compressed public key.
        if (x >= SecP256K1Curve.q) {
            // Cannot have point co-ordinates larger than this as everything takes place modulo Q.
            return null
        }
        // Compressed keys require you to know an extra bit of data about the y-coord as there are
        // two possibilities. So it's encoded in the recId.
        val R: ECPoint = decompressKey(x, recId and 1 == 1)
        //   1.4. If nR != point at infinity, then do another iteration of Step 1 (callers
        //        responsibility).
        if (!R.multiply(n).isInfinity) {
            return null
        }
        //   1.5. Compute e from M using Steps 2 and 3 of ECDSA signature verification.
        val e = BigInteger(1, message)
        //   1.6. For k from 1 to 2 do the following.   (loop is outside this function via
        //        iterating recId)
        //   1.6.1. Compute a candidate public key as:
        //               Q = mi(r) * (sR - eG)
        //
        // Where mi(x) is the modular multiplicative inverse. We transform this into the following:
        //               Q = (mi(r) * s ** R) + (mi(r) * -e ** G)
        // Where -e is the modular additive inverse of e, that is z such that z + e = 0 (mod n).
        // In the above equation ** is point multiplication and + is point addition (the EC group
        // operator).
        //
        // We can find the additive inverse by subtracting e from zero then taking the mod. For
        // example the additive inverse of 3 modulo 11 is 8 because 3 + 8 mod 11 = 0, and
        // -3 mod 11 = 8.
        val eInv = BigInteger.ZERO.subtract(e).mod(n)
        val rInv = sig.r.modInverse(n)
        val srInv = rInv.multiply(sig.s).mod(n)
        val eInvrInv = rInv.multiply(eInv).mod(n)
        val q = ECAlgorithms.sumOfTwoMultiplies(CURVE.g, eInvrInv, R, srInv)
        val qBytes = q.getEncoded(false)
        // We remove the prefix
        return BigInteger(1, qBytes.copyOfRange(1, qBytes.size))
    }

    private fun decompressKey(xBN: BigInteger, yBit: Boolean): ECPoint {
        val x9 = X9IntegerConverter()
        val compEnc: ByteArray = x9.integerToBytes(xBN, 1 + x9.getByteLength(CURVE.curve))
        compEnc[0] = (if (yBit) 0x03 else 0x02).toByte()
        return CURVE.curve.decodePoint(compEnc)
    }

    fun signBytes(message: ByteArray, keyPair: ECKeyPair): SignatureData {
        val publicKey = keyPair.publicKey
        val sig = keyPair.sign(message)
        return createSignatureData(sig, publicKey, message)
    }

    private fun createSignatureData(sig: ECDSASignature, publicKey: BigInteger, messageHash: ByteArray): SignatureData {
        // Now we have to work backwards to figure out the recId needed to recover the signature.
        var recId = -1
        for (i in 0..3) {
            val k = recoverFromSignature(i, sig, messageHash)
            if (k != null && k == publicKey) {
                recId = i
                break
            }
        }
        if (recId == -1) {
            throw RuntimeException(
                "Could not construct a recoverable key. Are your credentials valid?"
            )
        }
        val headerByte = recId + 27

        // 1 header + 32 bytes for R + 32 bytes for S
        val v = byteArrayOf(headerByte.toByte())
        val r = toBytesPadded(sig.r, 32)
        val s = toBytesPadded(sig.s, 32)
        return SignatureData(v, r, s)
    }

    fun publicKeyFromPrivate(privateKey: BigInteger): BigInteger {
        val point: ECPoint = publicPointFromPrivate(privateKey)
        val encoded = point.getEncoded(false)
        return BigInteger(1, Arrays.copyOfRange(encoded, 1, encoded.size)) // remove prefix
    }

    fun publicPointFromPrivate(privateKey: BigInteger): ECPoint {
        var privKey = privateKey
        if (privKey.bitLength() > CURVE.n.bitLength()) {
            privKey = privKey.mod(CURVE.n)
        }
        return FixedPointCombMultiplier().multiply(CURVE.g, privKey)
    }

    private fun toBytesPadded(value: BigInteger, length: Int): ByteArray {
        val result = ByteArray(length)
        val bytes = value.toByteArray()
        val bytesLength: Int
        val srcOffset: Int
        if (bytes[0].toInt() == 0) {
            bytesLength = bytes.size - 1
            srcOffset = 1
        } else {
            bytesLength = bytes.size
            srcOffset = 0
        }
        if (bytesLength > length) {
            throw RuntimeException("Input is too large to put in byte array of size $length")
        }
        val destOffset = length - bytesLength
        System.arraycopy(bytes, srcOffset, result, destOffset, bytesLength)
        return result
    }
}
