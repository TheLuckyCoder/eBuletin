package bugs.decentralized.utils


import bugs.decentralized.BlockchainApplication
import bugs.decentralized.model.AccountAddress
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.io.Serializable
import java.util.*

@Component
class JwtTokenUtil : Serializable {
    fun getAddressFromToken(token: String): AccountAddress {
        return AccountAddress(getClaimFromToken(token, Claims::getSubject))
    }

    fun getExpirationDateFromToken(token: String): Date {
        return getClaimFromToken<Date>(token, Claims::getExpiration)
    }

    fun <T> getClaimFromToken(token: String, claimsResolver: Claims.() -> T): T {
        val claims: Claims = getAllClaimsFromToken(token)
        return claims.claimsResolver()
    }

    private fun getAllClaimsFromToken(token: String): Claims {
        return Jwts.parser()
            .setSigningKey(BlockchainApplication.KEYS.privateHex)
            .parseClaimsJws(token)
            .body
    }

    private fun isTokenExpired(token: String): Boolean {
        val expiration: Date = getExpirationDateFromToken(token)
        return expiration.before(Date())
    }

    fun generateToken(accountAddress: AccountAddress): String {
        return doGenerateToken(accountAddress.value)
    }

    private fun doGenerateToken(subject: String): String {
        val claims: Claims = Jwts.claims().setSubject(subject)
        claims["scopes"] = listOf(SimpleGrantedAuthority("ROLE_ADMIN"))
        return Jwts.builder()
            .setClaims(claims)
            .setIssuer(BlockchainApplication.NODE.address)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS * 1000))
            .signWith(SignatureAlgorithm.HS256, BlockchainApplication.KEYS.privateHex)
            .compact()
    }

    fun validateToken(token: String, accountAddress: AccountAddress): Boolean {
        val username = getAddressFromToken(token)
        if (isTokenExpired(token)) return false
        return try {
            check(username == accountAddress)
            accountAddress.validate()
            true
        } catch (e: IllegalStateException) {
            false
        }
    }

    companion object {
        const val ACCESS_TOKEN_VALIDITY_SECONDS = 60 * 30
    }
}
