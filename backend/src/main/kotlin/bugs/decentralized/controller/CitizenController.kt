package bugs.decentralized.controller

import bugs.decentralized.model.IdCard
import bugs.decentralized.model.PublicAccountKey
import bugs.decentralized.repository.BlockRepository
import bugs.decentralized.repository.getInformationAtAddress
import bugs.decentralized.utils.RSA
import bugs.decentralized.utils.StringMap
import bugs.decentralized.utils.decodeHex
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import javax.crypto.spec.SecretKeySpec

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
    @GetMapping("/buletin/{publicKey}")
    fun getIdCard(@PathVariable publicKey: PublicAccountKey): String {
        val address = publicKey.toAddress()

        val idCardMap = StringMap()
        blockRepository.getInformationAtAddress(address) {
            it.idCard?.let { idCard ->
                idCardMap.putAll(idCard)
            }
        }

        val id = IdCard(
            cnp = idCardMap["cnp"]!!.toUInt(),
            lastName = idCardMap["lastName"]!!,
            firstName = idCardMap["firstName"]!!,
            address = idCardMap["lastName"]!!,
            birthLocation = idCardMap["birthLocation"]!!,
            birthDate = LocalDate(1, 1, 1),
            sex = idCardMap["sex"]!![0],
            issuedBy = idCardMap["issuedBy"]!!,
            series = idCardMap["series"]!!,
            number = idCardMap["number"]!!.toUInt(),
            validity = LocalTime(1, 1, 1)
        )

        val decodedAddress = publicKey.value.decodeHex()
        val key = SecretKeySpec(decodedAddress, 0, decodedAddress.size, "RSA")

        return RSA.encrypt(id, key)
    }
}
