package bugs.decentralized.controller

import bugs.decentralized.BlockchainApplication
import bugs.decentralized.controller.service.NodesService
import bugs.decentralized.model.AccountAddress
import bugs.decentralized.model.PublicAccountKey
import bugs.decentralized.model.Roles
import bugs.decentralized.model.Transaction
import bugs.decentralized.model.TransactionData
import bugs.decentralized.model.information.DriverLicense
import bugs.decentralized.model.information.IdCard
import bugs.decentralized.model.information.MedicalCard
import bugs.decentralized.repository.BlockRepository
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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
) {

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
