package net.uiqui.oauth.mock.boundary

import net.uiqui.oauth.mock.HttpTestClient
import net.uiqui.oauth.mock.contentType
import net.uiqui.oauth.mock.entity.JWKS
import net.uiqui.oauth.mock.entity.PublicKey
import net.uiqui.oauth.mock.fromJson
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.InetSocketAddress
import java.net.URI

internal class KeysServletTest {
    private var http: HttpServer? = null

    @BeforeEach
    fun init() {
        http = HttpServer(InetSocketAddress("localhost", 0)).apply {
            start()
        }
    }

    @AfterEach
    fun cleanUp() {
        http!!.stop()
    }

    @Test
    fun `test keys servlet`() {
        // given
        val jwks = JWKS(
            keys = listOf(
                PublicKey(
                    kty = "kty value",
                    e = "kty value",
                    use = "kty value",
                    kid = "kty value",
                    n = "kty value",
                )
            )
        )
        val servlet = KeysServlet(jwks)
        http!!.addServlet("/keys", servlet)
        // when
        val response = HttpTestClient.get(URI("${http!!.getHost()}/keys"))
        // then
        assertThat(response.statusCode()).isEqualTo(200)
        assertThat(response.contentType()).isEqualTo("application/json;charset=utf-8")
        assertThat(response.fromJson(JWKS::class)).isEqualTo(jwks)
    }
}
