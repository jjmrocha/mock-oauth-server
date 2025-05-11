package net.uiqui.oauth.mock

import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSObject
import net.uiqui.oauth.mock.jwks.JWKS
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.net.http.HttpResponse

internal class OAuthServerMockTest {
    @Test
    fun `test generateJWT without starting mock server`() {
        // given
        val classUnderTest = OAuthServerMock()
        // when
        assertThrows<IllegalStateException> {
            classUnderTest.generateJWT(emptyMap())
        }
    }

    @Test
    fun `test generateJWT`() {
        // given
        val claims =
            mapOf(
                "aud" to "audience",
                "iss" to "issuer",
            )
        val classUnderTest = OAuthServerMock()
        // when
        classUnderTest.start()
        val jwks = classUnderTest.getJwksUri()
        val jwt = classUnderTest.generateJWT(claims)
        classUnderTest.shutdown()
        // then
        val header = JWSObject.parse(jwt).header
        assertThat(header.keyID).isNotNull
        assertThat(header.algorithm).isNotNull
        assertThat(header.jwkurl).isEqualTo(jwks)
        assertThat(header.type).isEqualTo(JOSEObjectType.JWT)
        val payload = JWSObject.parse(jwt).payload.toJSONObject()
        assertThat(payload["aud"]).isEqualTo("audience")
        assertThat(payload["iss"]).isEqualTo("issuer")
    }

    @Test
    fun `test getJwksUri without starting mock server`() {
        // given
        val classUnderTest = OAuthServerMock()
        // when
        assertThrows<IllegalStateException> {
            classUnderTest.getJwksUri()
        }
    }

    @Test
    fun `test getJwksUri`() {
        // given
        val classUnderTest = OAuthServerMock()
        // when
        classUnderTest.start()
        val result = classUnderTest.getJwksUri()
        classUnderTest.shutdown()
        // then
        assertThat(result.scheme).isEqualTo("http")
        assertThat(result.host).isEqualTo("localhost")
        assertThat(result.path).isEqualTo("/.well-known/jwks.json")
    }

    @Test
    fun `test start`() {
        // given
        val classUnderTest = OAuthServerMock()
        println("before start")
        classUnderTest.start()
        println("after start")
        // when
        val response = HttpTestClient.get(classUnderTest.getJwksUri())
        // then
        assertThat(response.statusCode()).isEqualTo(200)
        assertThat(response.contentType()).isEqualTo("application/json")
        // check jwks
        assertValidJWKS(response)
        // clean up
        classUnderTest.shutdown()
    }

    @Test
    fun `test shutdown`() {
        // given
        val classUnderTest = OAuthServerMock()
        classUnderTest.start()
        // stop
        classUnderTest.shutdown()
        // start again
        classUnderTest.start()
        // then
        val response = HttpTestClient.get(classUnderTest.getJwksUri())
        // then
        assertThat(response.statusCode()).isEqualTo(200)
        assertThat(response.contentType()).isEqualTo("application/json")
        // check jwks
        assertValidJWKS(response)
        // clean up
        classUnderTest.shutdown()
    }

    private fun assertValidJWKS(response: HttpResponse<String>) {
        val jwks = response.fromJson(JWKS::class)
        assertThat(jwks.keys).hasSize(1)
        jwks.keys.forEach { jwk ->
            assertThat(jwk.kty).isEqualTo("RSA")
            assertThat(jwk.e).isEqualTo("AQAB")
            assertThat(jwk.use).isEqualTo("sig")
            assertThat(jwk.kid).matches("[a-f\\d]{8}-[a-f\\d]{4}-[a-f\\d]{4}-[a-f\\d]{4}-[a-f\\d]{12}")
            assertThat(jwk.n).isNotNull
        }
    }
}
