package bugs.decentralized.controller

import bugs.decentralized.controller.service.EmailSenderService
import bugs.decentralized.model.AccountAddress
import bugs.decentralized.repository.BlockRepository
import bugs.decentralized.repository.EmailCodeRepository
import bugs.decentralized.repository.getInformationAtAddress
import bugs.decentralized.repository.getRoleOf
import bugs.decentralized.utils.JwtTokenUtil
import bugs.decentralized.utils.LoggerExtensions
import bugs.decentralized.utils.ecdsa.Sign
import bugs.decentralized.utils.ecdsa.SignatureData
import kotlinx.serialization.Serializable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController @Autowired constructor(
    private val jwtTokenUtil: JwtTokenUtil,
    private val blockRepository: BlockRepository,
    private val emailSenderService: EmailSenderService,
) {

    private val emailCodeRepository = EmailCodeRepository
    private val log = LoggerExtensions.getLogger<CitizenController>()

    @Serializable
    class SignedAddress(
        val address: AccountAddress,
        val signedAddress: SignatureData,
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

        log.info("Secret Code for ${lastEmail}: $secretCode")

        emailSenderService.sendMail(lastEmail!!, "Register to government", secretCode.toString())

        return ResponseEntity.ok("")
    }

    @PostMapping("/loginWithCode")
    fun loginWithCode(@RequestBody signedAddress: SignedAddressWithCode): ResponseEntity<String> {
        Sign.checkAddress(signedAddress.address, signedAddress.address.value, signedAddress.signedAddress)
            ?: return ResponseEntity.status(401).build()

        val emailCode = emailCodeRepository.getExistingCodeForAccount(signedAddress.address)
        if (signedAddress.code != emailCode?.secretCode) {
            return ResponseEntity.status(401).body("Invalid Email code: ${signedAddress.code} (Correct Code: ${emailCode?.secretCode}")
        }

        val role = blockRepository.getRoleOf(signedAddress.address)
            ?: return ResponseEntity.status(401).body("No role found, cannot generate token")

        return ResponseEntity.ok(
            jwtTokenUtil.generateToken(signedAddress.address, role)
        )
    }
}
