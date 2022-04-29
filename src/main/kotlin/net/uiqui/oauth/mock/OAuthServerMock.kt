package net.uiqui.oauth.mock

import net.uiqui.oauth.mock.http.HttpServer
import net.uiqui.oauth.mock.jwks.JWKSHandler
import net.uiqui.oauth.mock.jwks.JWTGenerator
import java.net.URI

private const val JWKS_ENDPOINT = "/.well-known/jwks.json"

/**
 * OAuth Server Mock.
 *
 * This class mocks an oauth2 server, by generating valid signed JWTs.
 * The class also provides the endpoint from where the public keys can be downloaded.
 */
class OAuthServerMock {
    private val jwtGenerator = JWTGenerator()
    private val httpServer = HttpServer().apply {
        addHandler(JWKS_ENDPOINT, JWKSHandler(jwtGenerator.getJWKS()))
    }

    /**
     * Generate a signed JWT with the supplied claims.
     * The only claims automatically included are: nbf and exp
     *
     * @param claims map with claims to include into the JWT.
     * @return signed JWT
     * @exception IllegalStateException if called without starting the mocked oauth server
     */
    fun generateJWT(claims: Map<String, Any>): String {
        return jwtGenerator.generate(getJwksUri(), claims)
    }

    /**
     * Returns the URI for JWKS.
     *
     * @return URL for JWKS
     * @exception IllegalStateException if called without starting the mocked oauth server
     */
    fun getJwksUri(): URI {
        if (!httpServer.isRunning()) throw IllegalStateException("JWKS Uri is only available after starting")
        return URI("${httpServer.getHost()}$JWKS_ENDPOINT")
    }

    /**
     * Starts the mocked oauth server.
     */
    fun start() {
        httpServer.start()
    }

    /**
     * Stops the mocked oauth server.
     */
    fun shutdown() {
        if (httpServer.isRunning()) {
            httpServer.stop()
        }
    }
}
