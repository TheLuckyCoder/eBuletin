package bugs.decentralized.controller

import bugs.decentralized.BlockchainApplication
import bugs.decentralized.model.AccountAddress
import bugs.decentralized.model.PublicAccountKey
import bugs.decentralized.model.Transaction
import bugs.decentralized.model.TransactionData
import bugs.decentralized.model.information.DriverLicense
import bugs.decentralized.model.information.IdCard
import bugs.decentralized.model.information.MedicalCard
import bugs.decentralized.repository.BlockRepository
import bugs.decentralized.repository.EmailCodeRepository
import bugs.decentralized.repository.NodesRepository
import bugs.decentralized.repository.TransactionsRepository
import bugs.decentralized.repository.getInformationAtAddress
import bugs.decentralized.repository.getTransactionsCountBy
import bugs.decentralized.utils.LoggerExtensions
import bugs.decentralized.utils.ecdsa.ECIES
import bugs.decentralized.utils.ecdsa.Sign
import bugs.decentralized.utils.ecdsa.SignatureData
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Used to communicate with the citizens
 */
@RestController
@RequestMapping("/citizen")
class CitizenController @Autowired constructor(
    private val blockRepository: BlockRepository,
    private val nodesRepository : NodesRepository,
    private val nodesService: NodesService,
) {

    private val emailCodeRepository = EmailCodeRepository
    private val transactionPool = TransactionsRepository
    private val log = LoggerExtensions.getLogger<CitizenController>()

    @Serializable
    class SignedAddress(
        val address: AccountAddress,
        val signedAddress: SignatureData,
    )

    @Serializable
    class SignedAddressWithEmail(
        val address: AccountAddress,
        val signedAddress: SignatureData,
        val email: String
    )

    @Serializable
    class SignedAddressWithCode(
        val address: AccountAddress,
        val signedAddress: SignatureData,
        val code: Int
    )

    @PostMapping("/login")
    fun login(@RequestBody signedAddress: SignedAddress): ResponseEntity<String> {
        Sign.checkAddress(signedAddress.address, signedAddress.address.value, signedAddress.signedAddress)
            ?: return ResponseEntity.status(401).build()

        var lastEmail: String? = null
        blockRepository.getInformationAtAddress(signedAddress.address) { information ->
            lastEmail = information.email
        }

        if (lastEmail == null) {
            return ResponseEntity.badRequest().body("Account not registered")
        }

        val secretCode = emailCodeRepository.generateCodeForEmail(signedAddress.address, lastEmail!!)

        log.info("Secret Code: $secretCode")

        // TODO Send email

        return ResponseEntity.ok("")
    }

    @PostMapping("/loginWithCode")
    fun loginWithCode(@RequestBody signedAddress: SignedAddressWithCode): ResponseEntity<Void> {
        Sign.checkAddress(signedAddress.address, signedAddress.address.value, signedAddress.signedAddress)
            ?: return ResponseEntity.status(401).build()

        val emailCode = emailCodeRepository.getExistingCodeForAccount(signedAddress.address)
        if (signedAddress.code != emailCode?.secretCode) {
            return ResponseEntity.status(401).build()
        }

        return ResponseEntity.ok().build()
    }

    @OptIn(DelicateCoroutinesApi::class)
    @PostMapping("/register")
    fun register(@RequestBody signedAddress: SignedAddressWithEmail): ResponseEntity<Void> {
        Sign.checkAddress(signedAddress.address, signedAddress.address.value, signedAddress.signedAddress)
            ?: return ResponseEntity.status(401).build()

        val nodeAddress = AccountAddress(BlockchainApplication.NODE.address)
        val transaction = Transaction.create(
            sender = nodeAddress,
            receiver = signedAddress.address,
            data = TransactionData(
                information = TransactionData.Information(email = signedAddress.email)
            ),
            keyPair = BlockchainApplication.KEYS,
            nonce = blockRepository.getTransactionsCountBy(nodeAddress).toULong(),
        )

        transactionPool.transactionsPool.add(transaction)

        val nodes = nodesRepository.findAll()
        nodes.asSequence()
            .filter { it != BlockchainApplication.NODE }
            .forEach { node ->
                GlobalScope.launch(Dispatchers.IO) {
                    log.info("Sending transaction to $node")
                    nodesService.sendTransaction(node.url, transaction)
                }
            }

        return ResponseEntity.ok().build()
    }

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
    fun getIdCard(@PathVariable publicKey: PublicAccountKey): ResponseEntity<String> {
        val address = publicKey.toAddress()

        val idCardMap = HashMap<String, String>()
        blockRepository.getInformationAtAddress(address) {
            it.idCard?.let { idCard ->
                idCardMap.putAll(idCard)
            }
        }

        return try {
            val id = IdCard.fromMap(idCardMap)
            ResponseEntity.ok(ECIES.encrypt(publicKey, id))
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/medical_card/{publicKey}")
    fun getMedicalCard(@PathVariable publicKey: PublicAccountKey): ResponseEntity<String> {
        val address = publicKey.toAddress()

        val map = HashMap<String, String>()
        blockRepository.getInformationAtAddress(address) {
            it.medicalCard?.let { medicalCard ->
                map.putAll(medicalCard)
            }
        }

        return try {
            val medicalCard = MedicalCard.fromMap(map)
            ResponseEntity.ok(ECIES.encrypt(publicKey, medicalCard))
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/driver_license/{publicKey}")
    fun getDriverLicense(@PathVariable publicKey: PublicAccountKey): ResponseEntity<String> {
        val address = publicKey.toAddress()

        val map = HashMap<String, String>()
        blockRepository.getInformationAtAddress(address) {
            it.driverLicense?.let { driverLicense ->
                map.putAll(driverLicense)
            }
        }

        return try {
            val driverLicense = DriverLicense.fromMap(map)
            ResponseEntity.ok(ECIES.encrypt(publicKey, driverLicense))
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }
}
