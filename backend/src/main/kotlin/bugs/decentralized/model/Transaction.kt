package bugs.decentralized.model

import bugs.decentralized.utils.SHA
import bugs.decentralized.utils.StringMap
import kotlinx.serialization.Serializable
import org.springframework.data.annotation.Id

@Serializable
data class Transaction(
    val sender: AccountAddress,
    // The receiver could very well be a new address (as we create an account when we first send data to it)
    val receiver: AccountAddress,
    val signature: String,
    val data: TransactionData,
    val nonce: ULong
) {
    @Id
    val hash = SHA.sha256(sender.value + receiver.value + signature + data + nonce)
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
