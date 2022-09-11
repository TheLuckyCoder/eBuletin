package bugs.decentralized.model

import bugs.decentralized.utils.SHA
import bugs.decentralized.utils.StringMap
import bugs.decentralized.utils.ecdsa.ECKeyPair
import bugs.decentralized.utils.ecdsa.Sign
import bugs.decentralized.utils.ecdsa.SignatureData
import bugs.decentralized.utils.toHexString
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.data.annotation.Id
import java.security.PrivateKey
import java.security.Signature

/**
 * See https://ethereum.org/en/developers/docs/transactions/
 *
 */
@Serializable
data class Transaction(
    val sender: AccountAddress,
    // The receiver could very well be a new address (as we create an account when we first send data to it)
    val receiver: AccountAddress,
    val data: TransactionData,
    /**
     * The signature is the [data] hashed with SHA-3 and signed with the senders private key
     * If valid, we are be able to derive form the [signature] and [data] the [PublicAccountKey] of the [sender]
     *
     * See https://goethereumbook.org/signature-verify/
     * https://ethereum.stackexchange.com/questions/13778/get-public-key-of-any-ethereum-account/13892
     */
    val signature: SignatureData,
    /**
     * a sequentially incrementing counter which indicate the transaction number from the account
     * This must be unique per sender
     */
    val nonce: ULong,
    @Id
    val hash: String = SHA.sha256(sender.value + receiver.value + data + nonce)
) {

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        private val json = Json {
            explicitNulls = false
        }

        fun create(
            sender: AccountAddress,
            receiver: AccountAddress,
            data: TransactionData,
            keyPair: ECKeyPair,
            nonce: ULong
        ): Transaction {
            val signature = Sign.signBytes(SHA.sha256Bytes(json.encodeToString(data)), keyPair)

            return Transaction(sender, receiver, data, signature, nonce)
        }
    }
}

/*{
    "transactionData": {
        "information": {
            "idCard": {
                "imageUrl": ....
            }
        }
    }
}*/

// All the kinds of data that a transaction can store
@Serializable
data class TransactionData(
    val information: Information? = null,
    val vote: String? = null,
) {

    @Serializable
    data class Information(
        val idCard: StringMap? = null, // The transaction doesn't need to contain an entire id card just the changed parts
        val driverLicense: StringMap? = null, // Same as above
        val medicalCard: StringMap? = null,
        val criminalRecord: StringMap? = null,
    )
}
