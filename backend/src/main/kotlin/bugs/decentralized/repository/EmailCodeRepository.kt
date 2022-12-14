package bugs.decentralized.repository

import bugs.decentralized.model.AccountAddress
import bugs.decentralized.model.EmailCode
import bugs.decentralized.utils.SHA
import java.security.SecureRandom
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes


object EmailCodeRepository {

    private val emailCodes = mutableListOf<EmailCode>()

    @Synchronized
    fun getExistingCodeForAccount(accountAddress: AccountAddress): EmailCode? {
        val code = emailCodes.firstOrNull { it.address == accountAddress }

        return when {
            code == null -> null
            code.isValid().not() -> {
                emailCodes.remove(code)
                null
            }
            else -> code
        }
    }

    @Synchronized
    fun generateCodeForEmail(accountAddress: AccountAddress, email: String): Int {
        getExistingCodeForAccount(accountAddress)?.let {
            return it.secretCode
        }

        val random = Random(SHA.sha256Hex(accountAddress.value + System.currentTimeMillis()).substring(0, 15).toLong(16))
        val code = random.nextInt(100000, 999999)
        val expirationTimestamp = System.currentTimeMillis().milliseconds + 30L.minutes

        val emailCode = EmailCode(
            address = accountAddress,
            email = email,
            secretCode = code,
            expirationTimestamp = expirationTimestamp.inWholeMilliseconds
        )

        emailCodes.add(emailCode)
        return emailCode.secretCode
    }

}
