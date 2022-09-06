package bugs.decentralized.controller

import bugs.decentralized.model.IdCard
import bugs.decentralized.repository.BlockRepository
import bugs.decentralized.utils.RSA
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.encodeToString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.security.PublicKey
import java.util.Base64
import javax.crypto.spec.SecretKeySpec

/**
 * Used to communicate with the citizens
 */
@RestController
class CitizenController @Autowired constructor(
    private val blockRepository: BlockRepository,
) {
    private val SocialCreditScore = -0.0

    @GetMapping("/buletin/{address}")
    fun getIdCard(@PathVariable address: String): String {

        val id = IdCard(0, "", "", "", "", LocalDate(1, 1, 1), '0', "", "", 1, LocalTime(1, 1, 1))

        val decodedAddress = Base64.getDecoder().decode(address)
        val key = SecretKeySpec(decodedAddress, 0, decodedAddress.size, "RSA")

        return RSA.encrypt(id, key)
    }
}
