package bugs.decentralized.controller

import bugs.decentralized.model.Transaction
import bugs.decentralized.model.information.IdCard
import bugs.decentralized.repository.BlockRepository
import bugs.decentralized.repository.NodesRepository
import bugs.decentralized.repository.TransactionsRepository
import bugs.decentralized.utils.LoggerExtensions
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.LocalDate
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Used to communicate between a node and a local special government application
 */
@RestController
@RequestMapping("/government")
class GovernmentController @Autowired constructor(
    private val nodesService: NodesService,
    private val blockRepository: BlockRepository,
    private val nodesRepository: NodesRepository,
    private val nodesController: NodesController,
) {

    private val log = LoggerExtensions.getLogger<NodesController>()
    private val transactionsRepository = TransactionsRepository

    @PutMapping("/submit_transaction")
    suspend fun submitTransaction(@RequestBody transaction: Transaction): ResponseEntity<String> = coroutineScope {
        val isTransactionValid: ResponseEntity<String>? =
            Transaction.checkTransaction(transaction, transactionsRepository, log, blockRepository)

        if (isTransactionValid != null)
            return@coroutineScope isTransactionValid

        val hash = transaction.hash

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

        nodesController.sendTransactionToAllNodes(transaction)
        log.info("Received new transaction from government")
        ResponseEntity.accepted().build()
    }
}
