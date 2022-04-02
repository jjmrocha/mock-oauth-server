package net.uiqui.oauth.mock

import net.uiqui.oauth.mock.boundary.HttpServer
import net.uiqui.oauth.mock.boundary.KeysServlet
import net.uiqui.oauth.mock.control.JWTGenerator
import java.net.InetSocketAddress
import java.net.URI

private const val JWKS_ENDPOINT = "/.well-known/jwks.json"

/**
 * OAuth Server Mock.
 *
 * This class mocks an oauth2 server, by generating valid signed JWTs.
 * The class also provides the endpoint from where the public keys can be downloaded.
 *
 * @param hostname the Host name, defaults to "localhost".
 * @param port the Host port number.
 * A port number of zero will let the system pick up an ephemeral port in a bind operation, defaults to 0.
 */
class OAuthServerMock(
    val hostname: String = "localhost",
    val port: Int = 0,
) {
    private val jwtGenerator = JWTGenerator()
    private val httpServer = HttpServer(InetSocketAddress(hostname, port))

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
        if (JWKS_ENDPOINT !in httpServer.getPaths()) {
            httpServer.addServlet(JWKS_ENDPOINT, KeysServlet(jwtGenerator.getJWKS()))
        }

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
