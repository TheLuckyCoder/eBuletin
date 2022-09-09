package bugs.decentralized.model

import bugs.decentralized.utils.SHA
import bugs.decentralized.utils.StringMap
import kotlinx.serialization.Serializable
import org.springframework.data.annotation.Id

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
     * If valid, we should be able to derive a [PublicAccountKey] form the [signature] and [data]
     * which then should lead to a valid [AccountAddress]
     *
     * See https://goethereumbook.org/signature-verify/
     * https://ethereum.stackexchange.com/questions/13778/get-public-key-of-any-ethereum-account/13892
     */
    val signature: String,
    /**
     * a sequentially incrementing counter which indicate the transaction number from the account
     * This must be unique per sender
     */
    val nonce: ULong,
    // TODO: Should we include the signature in the hash?
    @Id
    val hash: String = SHA.sha256(sender.value + receiver.value + data + signature + nonce)
)

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
