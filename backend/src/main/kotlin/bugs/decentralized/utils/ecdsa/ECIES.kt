package bugs.decentralized.utils.ecdsa

import bugs.decentralized.model.PublicAccountKey
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bouncycastle.crypto.BufferedBlockCipher
import org.bouncycastle.crypto.CipherParameters
import org.bouncycastle.crypto.InvalidCipherTextException
import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.engines.AESEngine
import org.bouncycastle.crypto.generators.HKDFBytesGenerator
import org.bouncycastle.crypto.modes.GCMBlockCipher
import org.bouncycastle.crypto.params.HKDFParameters
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.crypto.params.ParametersWithIV
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.ECPointUtil
import org.bouncycastle.jce.interfaces.ECPrivateKey
import org.bouncycastle.jce.interfaces.ECPublicKey
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec
import org.bouncycastle.jce.spec.ECNamedCurveSpec
import org.bouncycastle.util.Arrays
import org.bouncycastle.util.encoders.Hex
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.InvalidAlgorithmParameterException
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.SecureRandom
import java.security.spec.ECPrivateKeySpec
import java.security.spec.ECPublicKeySpec
import java.security.spec.InvalidKeySpecException
import javax.crypto.NoSuchPaddingException

object ECIES {

    private class AESGCMBlockCipher : BufferedBlockCipher() {
        private val internalCipher = GCMBlockCipher(AESEngine())

        override fun init(forEncryption: Boolean, params: CipherParameters?) {
            internalCipher.init(forEncryption, params)
        }

        override fun getOutputSize(len: Int): Int {
            return internalCipher.getOutputSize(len)
        }

        @Throws(InvalidCipherTextException::class)
        override fun doFinal(out: ByteArray?, outOff: Int): Int {
            return internalCipher.doFinal(out, outOff)
        }

        override fun processBytes(`in`: ByteArray?, inOff: Int, len: Int, out: ByteArray?, outOff: Int): Int {
            return internalCipher.processBytes(`in`, inOff, len, out, outOff)
        }
    }

    private const val CURVE_NAME = "secp256k1"
    private const val UNCOMPRESSED_PUBLIC_KEY_SIZE = 65
    private const val AES_IV_LENGTH = 16
    private const val AES_TAG_LENGTH = 16
    private const val AES_IV_PLUS_TAG_LENGTH = AES_IV_LENGTH + AES_TAG_LENGTH
    private const val SECRET_KEY_LENGTH = 32
    private val SECURE_RANDOM: SecureRandom = SecureRandom()

    /**
     * Generates new key pair consists of [ECPublicKey] and [ECPrivateKey]
     *
     * @return new EC key pair
     */
    fun generateEcKeyPair(): SimpleKeyPair {
        val ecSpec: ECNamedCurveParameterSpec = ECNamedCurveTable.getParameterSpec(CURVE_NAME)
        val g: KeyPairGenerator = KeyPairGenerator.getInstance("EC", BouncyCastleProvider())
        g.initialize(ecSpec, SECURE_RANDOM)
        val keyPair: KeyPair = g.generateKeyPair()
        return SimpleKeyPair(
            (keyPair.private as ECPrivateKey).d.toByteArray(),
            (keyPair.public as ECPublicKey).q.getEncoded(false)
        )
    }

    /**
     * Encrypts given message with given public key in hex
     *
     * @param publicKeyHex EC public key in hex
     * @param message message to encrypt
     * @return encrypted message
     */
    fun encrypt(publicKeyHex: String, message: String): String {
        val publicKey: ByteArray = Hex.decode(publicKeyHex)
        val encrypt = encrypt(publicKey, message.toByteArray())
        return Hex.toHexString(encrypt)
    }

    /**
     * Decrypts given ciphertext with given private key
     *
     * @param privateKeyHex EC private key in hex
     * @param ciphertext ciphered text in hex
     * @return decrypted message
     */
    fun decrypt(privateKeyHex: String, ciphertext: String): String {
        val privateKey: ByteArray = Hex.decode(privateKeyHex)
        val cipherBytes: ByteArray = Hex.decode(ciphertext)
        return String(decrypt(privateKey, cipherBytes), StandardCharsets.UTF_8)
    }

    /**
     * Encrypts given message with given public key
     *
     * @param publicKeyBytes EC public key binary
     * @param message message to encrypt binary
     * @return encrypted message binary
     */
    fun encrypt(publicKeyBytes: ByteArray, message: ByteArray): ByteArray {
        val ecSpec: ECNamedCurveParameterSpec = ECNamedCurveTable.getParameterSpec(CURVE_NAME)
        val pair: KeyPair = generateEphemeralKey(ecSpec)
        val ephemeralPrivKey: ECPrivateKey = pair.private as ECPrivateKey
        val ephemeralPubKey: ECPublicKey = pair.public as ECPublicKey

        //generate receiver PK
        val keyFactory = getKeyFactory()
        val curvedParams =
            ECNamedCurveSpec(CURVE_NAME, ecSpec.curve, ecSpec.g, ecSpec.n)
        val publicKey: ECPublicKey = getEcPublicKey(curvedParams, publicKeyBytes, keyFactory)

        //Derive shared secret
        val uncompressed: ByteArray = ephemeralPubKey.q.getEncoded(false)
        val multiply: ByteArray = publicKey.q.multiply(ephemeralPrivKey.d).getEncoded(false)
        val aesKey = hkdf(uncompressed, multiply)

        // AES encryption
        return aesEncrypt(message, ephemeralPubKey, aesKey)
    }

    /**
     * Decrypts given ciphertext with given private key
     *
     * @param privateKeyBytes EC private key binary
     * @param cipherBytes cipher text binary
     * @return decrypted message binary
     */
    fun decrypt(privateKeyBytes: ByteArray, cipherBytes: ByteArray): ByteArray {
        val ecSpec: ECNamedCurveParameterSpec = ECNamedCurveTable.getParameterSpec(CURVE_NAME)
        val keyFactory = getKeyFactory()
        val curvedParams = ECNamedCurveSpec(CURVE_NAME, ecSpec.curve, ecSpec.g, ecSpec.n)

        //generate receiver private key
        val privateKeySpec = ECPrivateKeySpec(BigInteger(1, privateKeyBytes), curvedParams)
        val receiverPrivKey: ECPrivateKey = keyFactory.generatePrivate(privateKeySpec) as ECPrivateKey

        //get sender pub key
        val senderPubKeyByte: ByteArray = cipherBytes.copyOf(UNCOMPRESSED_PUBLIC_KEY_SIZE)
        val senderPubKey: ECPublicKey = getEcPublicKey(curvedParams, senderPubKeyByte, keyFactory)

        //decapsulate
        val uncompressed: ByteArray = senderPubKey.q.getEncoded(false)
        val multiply: ByteArray = senderPubKey.q.multiply(receiverPrivKey.d).getEncoded(false)
        val aesKey = hkdf(uncompressed, multiply)

        // AES decryption
        return aesDecrypt(cipherBytes, aesKey)
    }

    @Throws(NoSuchAlgorithmException::class)
    private fun getKeyFactory(): KeyFactory {
        return KeyFactory.getInstance("EC", BouncyCastleProvider())
    }

    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        NoSuchProviderException::class,
        InvalidCipherTextException::class
    )
    private fun aesEncrypt(message: ByteArray, ephemeralPubKey: ECPublicKey, aesKey: ByteArray): ByteArray {
        val aesgcmBlockCipher = AESGCMBlockCipher()
        val nonce = ByteArray(AES_IV_LENGTH)
        SECURE_RANDOM.nextBytes(nonce)
        val parametersWithIV = ParametersWithIV(KeyParameter(aesKey), nonce)
        aesgcmBlockCipher.init(true, parametersWithIV)
        val outputSize: Int = aesgcmBlockCipher.getOutputSize(message.size)
        var encrypted = ByteArray(outputSize)
        val pos: Int = aesgcmBlockCipher.processBytes(message, 0, message.size, encrypted, 0)
        aesgcmBlockCipher.doFinal(encrypted, pos)
        val tag: ByteArray = Arrays.copyOfRange(encrypted, encrypted.size - nonce.size, encrypted.size)
        encrypted = Arrays.copyOfRange(encrypted, 0, encrypted.size - tag.size)
        val ephemeralPkUncompressed: ByteArray = ephemeralPubKey.q.getEncoded(false)
        return Arrays.concatenate(ephemeralPkUncompressed, nonce, tag, encrypted)
    }

    @Throws(NoSuchAlgorithmException::class, InvalidAlgorithmParameterException::class)
    private fun generateEphemeralKey(ecSpec: ECNamedCurveParameterSpec): KeyPair {
        val g: KeyPairGenerator = KeyPairGenerator.getInstance("EC", BouncyCastleProvider())
        g.initialize(ecSpec, SECURE_RANDOM)
        return g.generateKeyPair()
    }

    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        NoSuchProviderException::class,
        InvalidCipherTextException::class
    )
    private fun aesDecrypt(inputBytes: ByteArray, aesKey: ByteArray): ByteArray {
        val encrypted: ByteArray = Arrays.copyOfRange(inputBytes, UNCOMPRESSED_PUBLIC_KEY_SIZE, inputBytes.size)
        val nonce: ByteArray = Arrays.copyOf(encrypted, AES_IV_LENGTH)
        val tag: ByteArray = Arrays.copyOfRange(encrypted, AES_IV_LENGTH, AES_IV_PLUS_TAG_LENGTH)
        val ciphered: ByteArray = Arrays.copyOfRange(encrypted, AES_IV_PLUS_TAG_LENGTH, encrypted.size)
        val aesgcmBlockCipher = AESGCMBlockCipher()
        val parametersWithIV = ParametersWithIV(KeyParameter(aesKey), nonce)
        aesgcmBlockCipher.init(false, parametersWithIV)
        val outputSize: Int = aesgcmBlockCipher.getOutputSize(ciphered.size + tag.size)
        val decrypted = ByteArray(outputSize)
        var pos: Int = aesgcmBlockCipher.processBytes(ciphered, 0, ciphered.size, decrypted, 0)
        pos += aesgcmBlockCipher.processBytes(tag, 0, tag.size, decrypted, pos)
        aesgcmBlockCipher.doFinal(decrypted, pos)
        return decrypted
    }

    private fun hkdf(uncompressed: ByteArray, multiply: ByteArray): ByteArray {
        val master = Arrays.concatenate(uncompressed, multiply)
        val hkdfBytesGenerator = HKDFBytesGenerator(SHA256Digest())
        hkdfBytesGenerator.init(HKDFParameters(master, null, null))
        val aesKey = ByteArray(SECRET_KEY_LENGTH)
        hkdfBytesGenerator.generateBytes(aesKey, 0, aesKey.size)
        return aesKey
    }

    @Throws(InvalidKeySpecException::class)
    private fun getEcPublicKey(
        curvedParams: ECNamedCurveSpec,
        senderPubKeyByte: ByteArray,
        keyFactory: KeyFactory
    ): ECPublicKey {
        val point = ECPointUtil.decodePoint(curvedParams.curve, senderPubKeyByte)
        val pubKeySpec = ECPublicKeySpec(point, curvedParams)
        return keyFactory.generatePublic(pubKeySpec) as ECPublicKey
    }


    inline fun <reified T> encrypt(publicKeyHex: PublicAccountKey, data: T): String =
        encrypt(publicKeyHex.value, Json.encodeToString(data))
}
