package net.uiqui.oauth.mock.example

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.jose4j.jwa.AlgorithmConstraints
import org.jose4j.jwk.HttpsJwks
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import org.jose4j.keys.resolvers.HttpsJwksVerificationKeyResolver
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val authentication: AuthenticationConfig
) : OncePerRequestFilter() {
    private val jwtConsumer = lazy {
        JwtConsumerBuilder()
            .setRequireExpirationTime()
            .setExpectedIssuer(authentication.oauthIssuer)
            .setExpectedAudience(authentication.oauthAudience)
            .setVerificationKeyResolver(
                HttpsJwksVerificationKeyResolver(
                    HttpsJwks(authentication.jwksEndpoint)
                )
            )
            .setJwsAlgorithmConstraints(AlgorithmConstraints.NO_CONSTRAINTS)
            .build()
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val jwtToken = getJwtOrNull(request)

            if (jwtToken != null) {
                val authToken = createJwtAuthenticationToken(jwtToken)
                SecurityContextHolder.getContext().authentication = authToken
            }
        } catch (e: Exception) {
            logger.error("Error parsing JWT: ${e.localizedMessage}")
        } finally {
            filterChain.doFilter(request, response)
        }
    }

    private fun createJwtAuthenticationToken(jwtToken: String): Authentication {
        val claims = jwtConsumer.value.processToClaims(jwtToken)
        val appId = claims.getClaimValueAsString("appid")

        return object : AbstractAuthenticationToken(emptyList()) {
            init {
                isAuthenticated = true
            }

            override fun getCredentials(): Any = jwtToken
            override fun getPrincipal(): Any = appId
        }
    }

    private fun getJwtOrNull(request: HttpServletRequest): String? {
        val authHeader = request.getHeader("Authorization") ?: return null
        return if (authHeader.contains("Bearer "))
            authHeader.replace("Bearer ", "").trim()
        else null
    }
}
