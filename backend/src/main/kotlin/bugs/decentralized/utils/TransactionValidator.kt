package bugs.decentralized.utils

import bugs.decentralized.model.PublicAccountKey
import bugs.decentralized.model.Transaction
import bugs.decentralized.utils.ecdsa.Sign
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bouncycastle.util.encoders.Hex
import java.security.SignatureException

class InvalidTransactionException(message: String) : Exception(message)

object TransactionValidator {

    /**
     * @return true if valid
     */
    @Throws(InvalidTransactionException::class, SignatureException::class)
    fun verifySignature(transaction: Transaction) {
        val publicKey = Sign.signedMessageToKey(Json.encodeToString(transaction.data), transaction.signature)
        val receiverPublicAccountKey = PublicAccountKey(Hex.toHexString(publicKey.toByteArray()))
        val toAddress = receiverPublicAccountKey.toAddress()

        if (transaction.sender != toAddress) {
            throw InvalidTransactionException("Transaction's senders address and signature don't match")
        }
    }
}
