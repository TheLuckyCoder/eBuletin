package bugs.decentralized.controller.filter

import bugs.decentralized.model.AccountAddress
import bugs.decentralized.utils.JwtTokenUtil
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.SignatureException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter @Autowired constructor(
    private val jwtTokenUtil: JwtTokenUtil
) : OncePerRequestFilter() {



    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.requestURI
        if (path.contains("login") || path.contains("register")) {
            filterChain.doFilter(request, response)
            return
        }
        val header = request.getHeader(HEADER_STRING)
        var accountAddress: AccountAddress? = null
        var authToken: String? = null
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            authToken = header.replace(TOKEN_PREFIX, "")
            try {
                accountAddress = jwtTokenUtil.getAddressFromToken(authToken)
            } catch (e: IllegalArgumentException) {
                logger.error("an error occured during getting username from token", e)
            } catch (e: ExpiredJwtException) {
                logger.warn("the token is expired and not valid anymore", e)
            } catch (e: SignatureException) {
                logger.error("Authentication Failed. Username or Password not valid.")
            }
        } else {
            logger.warn("couldn't find bearer string, will ignore the header")
        }

        if (authToken != null && accountAddress != null && SecurityContextHolder.getContext().authentication == null) {
            if (jwtTokenUtil.validateToken(authToken, accountAddress)) {
                val authentication = UsernamePasswordAuthenticationToken(
                    null,
                    null,
                    listOf(SimpleGrantedAuthority("ROLE_ADMIN"))
                )
                authentication.setDetails(WebAuthenticationDetailsSource().buildDetails(request))
                logger.info("authenticated user $accountAddress, setting security context")
                SecurityContextHolder.getContext().authentication = authentication
            }
        }
        filterChain.doFilter(request, response)
    }

    companion object {
        const val TOKEN_PREFIX = "Bearer "
        const val HEADER_STRING = "Authorization"
    }
}
