package bugs.decentralized.ecsda

import bugs.decentralized.utils.SHA
import bugs.decentralized.utils.ecdsa.ECIES
import bugs.decentralized.utils.ecdsa.ECKeyPair
import bugs.decentralized.utils.ecdsa.Sign
import org.bouncycastle.asn1.x509.ObjectDigestInfo.publicKey
import org.bouncycastle.util.encoders.Hex
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigInteger
import java.nio.charset.Charset

@SpringBootTest
class ECSDATest {

    @Test
    fun testSigning() {
        val str = "Hello"
        val originalKeyPair = ECIES.generateEcKeyPair()

        val keyPair = ECKeyPair(
            BigInteger(originalKeyPair.privateHex, 16),
            BigInteger(originalKeyPair.getPublicHex(false), 16)
        )
        val signatureData = Sign.signBytes(SHA.sha256Bytes(str), keyPair)
        val publicKey = Sign.signedMessageToKey(str, signatureData)


        val encode = publicKey.toString(16)
        assert(encode == originalKeyPair.getPublicHex(false))
    }
}
