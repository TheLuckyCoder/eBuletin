package bugs.decentralized.ecsda

import bugs.decentralized.utils.SHA
import bugs.decentralized.utils.ecdsa.ECIES
import bugs.decentralized.utils.ecdsa.Sign
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ECSDATest {

    @Test
    fun testEncryptionDecryption() {
        val str = "Hello"
        val originalKeyPair = ECIES.generateEcKeyPair()
        val encrypted = ECIES.encrypt(originalKeyPair.getPublicHex(false), str)
        val decrypted = ECIES.decrypt(originalKeyPair.privateHex, encrypted)

        check(str == decrypted)
    }

    @Test
    fun testSigning() {
        val str = "Hello"

        val keyPair = Sign.ECKeyPair.create(ECIES.generateEcKeyPair())
        val signatureData = Sign.signBytes(SHA.sha256Bytes(str), keyPair)
        val publicKey = Sign.signedMessageToKey(str, signatureData)

        check(keyPair.publicKey == publicKey)
    }
}
