package bugs.decentralized.model

import bugs.decentralized.utils.SHA
import bugs.decentralized.utils.StringMap
import bugs.decentralized.utils.ecdsa.Sign
import bugs.decentralized.utils.ecdsa.SignatureData
import bugs.decentralized.utils.ecdsa.SimpleKeyPair
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.data.annotation.Id

/**
 * See https://ethereum.org/en/developers/docs/transactions/
 *
 */
@Serializable
data class Transaction(
    @field:Id
    val hash: String,
    @SerialName("sender")
    private val _sender: String,
    // The receiver could very well be a new address (as we create an account when we first send data to it)
    @SerialName("receiver")
    private val _receiver: String,
    val data: TransactionData,
    /**
     * The signature is the [data] hashed with SHA-256 and signed with the senders private key
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
    val nonce: Long,
) {

    val sender: AccountAddress
        get() = AccountAddress(_sender)

    val receiver: AccountAddress
        get() = AccountAddress(_receiver)

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        private val json = Json {
            explicitNulls = false
        }

        fun create(
            sender: AccountAddress,
            receiver: AccountAddress,
            data: TransactionData,
            keyPair: SimpleKeyPair,
            nonce: ULong
        ): Transaction {
            val signKeys = Sign.ECKeyPair.from(keyPair)
            val signature = Sign.sign(json.encodeToString(data), signKeys)

            return Transaction(
                _sender = sender.value,
                _receiver = receiver.value,
                data = data,
                signature = signature,
                nonce = nonce.toLong(),
                hash = SHA.sha256Hex(sender.value + receiver.value + data + nonce)
            )
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
    val vote: Vote? = null,
    val votePermission: VotePermission? = null
) {

    @Serializable
    data class Information(
        val idCard: StringMap? = null, // The transaction doesn't need to contain an entire id card just the changed parts
        val driverLicense: StringMap? = null, // Same as above
        val medicalCard: StringMap? = null,
        val criminalRecord: StringMap? = null,
    )

    @Serializable
    data class Vote(
        val candidate: StringMap,//Candidate name + party
        val party: StringMap,//For elections where you only select the party not the candidate ex: parliamentary elections
        val electionType: String,// local/national/european
        val electionRound: Short,//Presidential elections in Romania have two rounds
        val electionYear: Short
    )

    @Serializable
    data class VotePermission(
        val electionType: String,// local/national/european
        val electionRound: Short,//Presidential elections in Romania have two rounds
        val electionYear: Short
    )
}
