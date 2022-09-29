package bugs.decentralized.configuration

import bugs.decentralized.model.Roles
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfiguration {
    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.cors().and().csrf().disable()
        http
            .authorizeHttpRequests { auth ->
                auth.anyRequest().permitAll()
//                auth.antMatchers("/citizen").hasRole(Roles.CITIZEN)
//                auth.antMatchers("/node").hasRole(Roles.NODE)
//                auth.antMatchers("/government").hasRole(Roles.GOVERNMENT)
            }
            .httpBasic(withDefaults())
        return http.build()
    }
}
