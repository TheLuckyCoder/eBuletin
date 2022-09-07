package bugs.decentralized.model

import bugs.decentralized.utils.SHA
import bugs.decentralized.utils.StringMap
import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val sender: AccountAddress,
    val receiver: AccountAddress,
    val signature: String,
    val data: TransactionData,
    val nonce: ULong
) {
    val hash = SHA.sha256(sender.value + receiver.value + signature + data + nonce)
}


// All the kinds of data that a transaction can store
@Serializable
data class TransactionData(
    val information: Information? = null,
    val vote: String? = null,
) {

    @Serializable
    data class Information(
        val idCard: StringMap? = null, // The transaction doesn't need to contain an entire id card just parts of it
        val driverLicense: StringMap? = null, // Same as above
    )
}
