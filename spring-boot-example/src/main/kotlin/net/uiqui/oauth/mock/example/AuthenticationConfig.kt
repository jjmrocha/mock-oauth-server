package net.uiqui.oauth.mock.example

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class AuthenticationConfig {
    @Value("\${oauth.jwks}")
    lateinit var jwksEndpoint: String

    @Value("\${oauth.issuer}")
    lateinit var oauthIssuer: String

    @Value("\${oauth.audience}")
    lateinit var oauthAudience: String
}
