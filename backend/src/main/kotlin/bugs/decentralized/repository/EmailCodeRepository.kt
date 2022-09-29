package bugs.decentralized.repository

import bugs.decentralized.model.AccountAddress
import bugs.decentralized.model.EmailCode
import java.security.SecureRandom
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes


object EmailCodeRepository {

    private val emailCodes = mutableListOf<EmailCode>()

    @Synchronized
    fun getExistingCodeForAccount(accountAddress: AccountAddress): EmailCode? {
        val code = emailCodes.find { it.address == accountAddress }

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

        val code = SecureRandom.getInstanceStrong().nextInt(100000, 999999)
        val expirationTimestamp = System.currentTimeMillis().milliseconds + 30L.minutes

        val emailCode = EmailCode(
            accountAddress,
            email,
            code,
            expirationTimestamp.inWholeMilliseconds
        )

        emailCodes.add(emailCode)
        return emailCode.secretCode
    }

}
