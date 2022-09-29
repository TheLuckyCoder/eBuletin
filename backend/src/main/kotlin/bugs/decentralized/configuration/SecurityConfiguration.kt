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
//                auth.antMatchers("/citizen/register").permitAll()
//                auth.antMatchers("/citizen/**").hasRole(Roles.CITIZEN)
//                auth.antMatchers("/node/**").hasAnyRole(Roles.NODE, Roles.ADMIN)
//                auth.antMatchers("/government/**").hasRole(Roles.GOVERNMENT)
                auth.anyRequest().permitAll()
            }
            .httpBasic(withDefaults())
        return http.build()
    }
}
