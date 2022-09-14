package bugs.decentralized.controller

import bugs.decentralized.model.information.IdCard
import bugs.decentralized.model.information.MedicalCard
import bugs.decentralized.model.PublicAccountKey
import bugs.decentralized.repository.BlockRepository
import bugs.decentralized.repository.getInformationAtAddress
import bugs.decentralized.utils.StringMap
import bugs.decentralized.utils.ecdsa.ECIES
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

/**
 * Used to communicate with the citizens
 */
@RestController
class CitizenController @Autowired constructor(
    private val blockRepository: BlockRepository,
) {

    /**
     * @param publicKey must be a HEX string representing a public address of an account
     *
     * Sends back a serialized [CardId] object, encrypted by the address
     */
    @GetMapping("/citizen/buletin/{publicKey}")
    fun getIdCard(@PathVariable publicKey: PublicAccountKey): String {
        val address = publicKey.toAddress()

        val idCardMap = StringMap()
        blockRepository.getInformationAtAddress(address) {
            it.idCard?.let { idCard ->
                idCardMap.putAll(idCard)
            }
        }

        val id = IdCard.fromMap(idCardMap)

        return ECIES.encrypt(publicKey, id)
    }

    @GetMapping("/citizen/medical_card/{publicKey}")
    fun getMedicalCard(@PathVariable publicKey: PublicAccountKey): String {
        val address = publicKey.toAddress()

        val map = StringMap()
        blockRepository.getInformationAtAddress(address) {
            it.medicalCard?.let { medicalCard ->
                map.putAll(medicalCard)
            }
        }

        val medicalCard = MedicalCard.fromMap(map)

        return ECIES.encrypt(publicKey, medicalCard)
    }
}
