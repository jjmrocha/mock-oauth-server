package net.uiqui.oauth.mock.example

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val authentication: AuthenticationConfig,
) {
    @Bean
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain? {
        // Disable sessions
        httpSecurity.sessionManagement { sessionManagement ->
            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }
        // Protect endpoints
        httpSecurity.authorizeHttpRequests { authorizeRequests ->
            authorizeRequests.anyRequest().authenticated()
        }
        // Disable Cross site request forgery
        httpSecurity.csrf { csrf ->
            csrf.disable()
        }
        // JWT authentication
        httpSecurity.addFilterBefore(
            JwtAuthenticationFilter(authentication),
            BasicAuthenticationFilter::class.java
        )
        // Error handling
        httpSecurity.exceptionHandling { exceptionHandling ->
            exceptionHandling.authenticationEntryPoint(HttpStatusEntryPoint(UNAUTHORIZED))
        }

        return httpSecurity.build()
    }
}
