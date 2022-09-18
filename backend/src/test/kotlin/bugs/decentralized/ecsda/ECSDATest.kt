package bugs.decentralized.ecsda

import bugs.decentralized.utils.ecdsa.ECIES
import bugs.decentralized.utils.ecdsa.Sign
import bugs.decentralized.utils.ecdsa.SimpleKeyPair
import org.bouncycastle.util.encoders.Hex
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.lang.StringBuilder
import java.math.BigInteger

@SpringBootTest
class ECSDATest {

    @Test
    fun testEncryptionDecryption() {
        val str = "Hello, World!"

        val eciesKeyPair = ECIES.generateEcKeyPair()
        val publicKeyHex = eciesKeyPair.publicHex
        val privateKeyHex = eciesKeyPair.privateHex

        run {
            val encrypted = ECIES.encrypt(publicKeyHex, str)
            val decrypted = ECIES.decrypt(privateKeyHex, encrypted)

            check(str == decrypted)
        }

        run {
            val keyPair = Sign.ECKeyPair.from(eciesKeyPair)
            val recoveredKeyPair = Sign.ECKeyPair.from(BigInteger(privateKeyHex, 16))
            check(keyPair.publicKey == recoveredKeyPair.publicKey)
            check(keyPair.privateKey == recoveredKeyPair.privateKey)

            val simpleKeyPair = SimpleKeyPair.from(recoveredKeyPair)
            val publicHex1 = simpleKeyPair.publicHex
            val privateHex1 = simpleKeyPair.privateHex
            check(publicKeyHex == publicHex1)
            check(privateKeyHex == privateHex1)

            val encrypted = ECIES.encrypt(publicHex1, str)
            val decrypted = ECIES.decrypt(privateHex1, encrypted)

            check(str == decrypted)
        }
    }

    @Test
    fun testSigning() {
        val str = "Hello"

        val generateEcKeyPair = ECIES.generateEcKeyPair()
        run {
            val keyPair = Sign.ECKeyPair.from(generateEcKeyPair)
            val signatureData = Sign.sign(str, keyPair)
            val publicKey = Sign.signedMessageToKey(str, signatureData)

            check(keyPair.publicKey == publicKey)
        }

        run {
            val keyPair = Sign.ECKeyPair.from(BigInteger(generateEcKeyPair.privateHex, 16))
            val signatureData = Sign.sign(str, keyPair)
            val publicKey = Sign.signedMessageToKey(str, signatureData)

            check(keyPair.publicKey == publicKey)
        }
    }
}
