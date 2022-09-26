package bugs.decentralized.controller

import bugs.decentralized.model.PublicAccountKey
import bugs.decentralized.model.Transaction
import bugs.decentralized.model.information.DriverLicense
import bugs.decentralized.model.information.IdCard
import bugs.decentralized.model.information.MedicalCard
import bugs.decentralized.repository.*
import bugs.decentralized.utils.LoggerExtensions
import bugs.decentralized.utils.ecdsa.ECIES
import kotlinx.coroutines.coroutineScope
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Used to communicate with the citizens
 */
@RestController
@RequestMapping("/citizen")
class CitizenController @Autowired constructor(
    private val blockRepository: BlockRepository,
    private val nodesRepository: NodesRepository,
    private val nodesService: NodesService,
    private val nodesController: NodesController
) {

    private val log = LoggerExtensions.getLogger<NodesController>()
    private val transactionsRepository = TransactionsRepository

    @GetMapping("/nonce/{publicKey}")
    fun nonce(@PathVariable publicKey: PublicAccountKey): String {
        val address = publicKey.toAddress()

        val count = blockRepository.getTransactionsCountBy(address)
        return ECIES.encrypt(publicKey, count.toString())
    }

    /**
     * @param publicKey must be a HEX string representing a public address of an account
     *
     * Sends back a serialized [IdCard] object, encrypted by the address
     */
    @GetMapping("/buletin/{publicKey}")
    fun getIdCard(@PathVariable publicKey: PublicAccountKey): String {
        val address = publicKey.toAddress()

        val idCardMap = HashMap<String, String>()
        blockRepository.getInformationAtAddress(address) {
            it.idCard?.let { idCard ->
                idCardMap.putAll(idCard)
            }
        }

        val id = IdCard.fromMap(idCardMap)

        return ECIES.encrypt(publicKey, id)
    }

    @GetMapping("/medical_card/{publicKey}")
    fun getMedicalCard(@PathVariable publicKey: PublicAccountKey): String {
        val address = publicKey.toAddress()

        val map = HashMap<String, String>()
        blockRepository.getInformationAtAddress(address) {
            it.medicalCard?.let { medicalCard ->
                map.putAll(medicalCard)
            }
        }

        val medicalCard = MedicalCard.fromMap(map)

        return ECIES.encrypt(publicKey, medicalCard)
    }

    @GetMapping("/driver_license/{publicKey}")
    fun getDriverLicense(@PathVariable publicKey: PublicAccountKey): String {
        val address = publicKey.toAddress()

        val map = HashMap<String, String>()
        blockRepository.getInformationAtAddress(address) {
            it.driverLicense?.let { driverLicense ->
                map.putAll(driverLicense)
            }
        }

        val driverLicense = DriverLicense.fromMap(map)

        return ECIES.encrypt(publicKey, driverLicense)
    }

    @PostMapping("/vote/{publicKey}")
    suspend fun vote(
        @RequestBody transaction: Transaction
    ): ResponseEntity<String> = coroutineScope {
        //check transaction
        val isTransactionValid: ResponseEntity<String>? =
            Transaction.checkTransaction(transaction, transactionsRepository, log, blockRepository)

        if (isTransactionValid != null)
            return@coroutineScope isTransactionValid

        if (!nodesController.blocks()
                .any { block ->
                    block.transactions.any {
                        it.data.votePermission?.electionData == transaction.data.vote?.electionData
                                && it.receiver == transaction.sender
                    }
                }
        ) return@coroutineScope ResponseEntity.badRequest().body("Invalid vote permission")

        nodesController.sendTransactionToAllNodes(transaction)
        log.info("Received new transaction from government")
        ResponseEntity.accepted().build()
    }
}
