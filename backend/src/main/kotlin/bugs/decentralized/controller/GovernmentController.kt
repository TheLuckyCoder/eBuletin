package bugs.decentralized.controller

import bugs.decentralized.BlockchainApplication
import bugs.decentralized.controller.service.NodesService
import bugs.decentralized.model.Roles
import bugs.decentralized.model.Transaction
import bugs.decentralized.model.information.IdCard
import bugs.decentralized.repository.BlockRepository
import bugs.decentralized.repository.NodesRepository
import bugs.decentralized.repository.TransactionsRepository
import bugs.decentralized.repository.getRoleOf
import bugs.decentralized.repository.getTransactionsCountBy
import bugs.decentralized.utils.LoggerExtensions
import bugs.decentralized.utils.SHA
import bugs.decentralized.utils.ecdsa.Sign
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bouncycastle.util.encoders.Hex
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.security.SignatureException

/**
 * Used to communicate between a node and a local special government application
 */
@RestController
class GovernmentController @Autowired constructor(
    private val nodesService: NodesService,
    private val blockRepository: BlockRepository,
    private val nodesRepository: NodesRepository,
) {

    private val log = LoggerExtensions.getLogger<NodesController>()
    private val transactionsRepository = TransactionsRepository

    @PutMapping("/submit_transaction")
    suspend fun submitTransaction(@RequestBody transaction: Transaction): ResponseEntity<String> = coroutineScope {
        log.info("Received new transaction")

        val nonceOfSender = blockRepository.getTransactionsCountBy(transaction.sender)
        val roleOfSender = blockRepository.getRoleOf(transaction.sender)

        val hash = transaction.hash

        if (hash != SHA.sha256Hex(
                transaction.sender.value + transaction.receiver.value + Json.encodeToString(
                    transaction.data
                ) + transaction.nonce
            )
        ) {
            log.warn("Invalid transaction hash")
            return@coroutineScope ResponseEntity.badRequest()
                .body("Invalid transaction hash")
        }

        if (transactionsRepository.getTransaction().any { it.hash == hash }) {
            log.warn("A transaction that already is in the pool has been received")
            return@coroutineScope ResponseEntity.badRequest()
                .body("A transaction that already is in the pool has been received")
        }

        val blocks = blockRepository.findAll()
        val transactionInBlocks = blocks.any { block ->
            block.transactions.any { it.hash == hash }
        }

        if (transactionInBlocks) {
            log.error("A transaction that already is in the blockchain has been received")
            return@coroutineScope ResponseEntity.badRequest()
                .body("A transaction that already is in the blockchain has been received")
        }

        try {
            if (Sign.checkAddressHash(
                    transaction.sender,
                    Hex.decode(transaction.hash),
                    transaction.signature
                ) == null
            ) {
//                throw SignatureException("Transaction's senders address and signature don't match")
            }
        } catch (e: SignatureException) {
            log.error(e.message)
            return@coroutineScope ResponseEntity.badRequest().body(e.message)
        }

        when (roleOfSender) {
            Roles.ADMIN -> Unit
            Roles.CITIZEN -> {
                return@coroutineScope ResponseEntity.badRequest()
                    .body("Sender doesn't have the necessary role ($roleOfSender)")
            }

            Roles.GOVERNMENT -> {
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
                        return@coroutineScope ResponseEntity.badRequest()
                            .body("It's the first transaction of this account, but the IdCard is not complete")
                    }
                }

                transaction.data.information?.idCard?.forEach { (key, value) ->
                    when (key) {
                        IdCard::cnp.name -> check(value.length == 13)
                        IdCard::lastName.name -> check(value.length >= 3)
                        IdCard::firstName.name -> check(value.length >= 3)
                        IdCard::address.name -> check(value.length >= 5)
                        IdCard::birthLocation.name -> check(value.length >= 3)
                        IdCard::sex.name -> check(value == "M" || value == "F")
                        IdCard::issuedBy.name -> check(value.length >= 5)
                        IdCard::series.name -> check(value.length == 2)
                        IdCard::seriesNumber.name -> check(value.length == 6)
                        IdCard::validity.name -> Json.decodeFromString<LocalDate>(value)
                    }
                }
            }

            else -> {
                if (nonceOfSender != 0UL) {
                    return@coroutineScope ResponseEntity.badRequest()
                        .body("This sender has already submit a transaction")
                }
            }
        }

        if (nonceOfSender != transaction.nonce.toULong()) {
            return@coroutineScope ResponseEntity.badRequest()
                .body("Nonce doesn't match")
        }

        val nodes = nodesRepository.findAll()
        nodes.asSequence()
            .filter { it != BlockchainApplication.NODE }
            .forEach { node ->
                launch(Dispatchers.IO) {
                    log.info("Sending transaction to $node")
                    nodesService.sendTransaction(node.url, transaction)
                }
            }

        transactionsRepository.transactionsPool.add(transaction)
        log.info("Received new transaction from government")

        ResponseEntity.accepted().build()
    }
}
