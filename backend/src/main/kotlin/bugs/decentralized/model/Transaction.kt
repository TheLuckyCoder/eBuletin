package bugs.decentralized.model

import bugs.decentralized.model.information.IdCard
import bugs.decentralized.repository.BlockRepository
import bugs.decentralized.repository.TransactionsRepository
import bugs.decentralized.utils.*
import bugs.decentralized.utils.ecdsa.Sign
import bugs.decentralized.utils.ecdsa.SignatureData
import bugs.decentralized.utils.ecdsa.SimpleKeyPair
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.springframework.data.annotation.Id
import org.springframework.http.ResponseEntity
import java.security.SignatureException

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
     * a sequentially incrementing counter which indicates the transaction number from the account
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

        fun checkTransaction(
            transaction: Transaction,
            transactionsRepository: TransactionsRepository,
            log: Logger = LoggerExtensions.getLogger<Transaction>(),
            blockRepository: BlockRepository
        ): ResponseEntity<String>? {
            log.info("Received new transaction")
            val hash = transaction.hash

            if (transactionsRepository.getTransaction().any { it.hash == hash }) {
                log.warn("A transaction that already is in the pool has been received")
                return ResponseEntity.badRequest()
                    .body("A transaction that already is in the pool has been received")
            }

            val blocks = blockRepository.findAll()
            val transactionInBlocks = blocks.any { block ->
                block.transactions.any { it.hash == hash }
            }

            if (transactionInBlocks) {
                log.error("A transaction that already is in the blockchain has been received")
                return ResponseEntity.badRequest()
                    .body("A transaction that already is in the blockchain has been received")
            }

            try {
                TransactionValidator.verifySignature(transaction)
            } catch (e: SignatureException) {
                log.error("Transaction has an invalid signature")
                return ResponseEntity.badRequest()
                    .body("Transaction has an invalid signature")
            } catch (e: InvalidTransactionException) {
                log.error(e.message)
                return ResponseEntity.badRequest().body(e.message)
            }

            val isNotTheFirstTransactionWithThisReceiver = blocks.any { block ->
                block.transactions.any { it.receiver == transaction.receiver }
            }

            if (!isNotTheFirstTransactionWithThisReceiver) {
                // The first transaction should always contain all the id's information
                try {
                    IdCard.fromMap(transaction.data.information!!.idCard!!)
                    // We don't need the value, just the exception if the value doesn't exist :)
                } catch (e: Exception) {
                    log.error("It's the first transaction of this account, but the IdCard is not complete", e)
                    return ResponseEntity.badRequest()
                        .body("It's the first transaction of this account, but the IdCard is not complete")
                }
            }

            return null
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
        val candidate: String? = null, // For some elections this is not necessary
        val party: String,
        val electionData: ElectionData
    )

    @Serializable
    data class VotePermission(
        val electionData: ElectionData,
        val pollingStation: PollingStation
    )

    @Serializable
    data class ElectionData(
        val electionType: String, // local / national / european
        val electionRound: Short, // Presidential elections in Romania have two rounds
        val electionYear: Short,
    )
}

object ElectionTypes {
    const val p = "Presidential"
    const val l = "Local"
    const val eu = "European"
}
