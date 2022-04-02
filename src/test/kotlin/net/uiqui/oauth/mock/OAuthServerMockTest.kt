package net.uiqui.oauth.mock

import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSObject
import net.uiqui.oauth.mock.entity.JWKS
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class OAuthServerMockTest {

    @Test
    fun `test constructor without parameters`() {
        // given
        val classUnderTest = OAuthServerMock()
        // then
        assertThat(classUnderTest.hostname).isEqualTo("localhost")
        assertThat(classUnderTest.port).isEqualTo(0)
    }

    @Test
    fun `test constructor with parameters`() {
        // given
        val classUnderTest = OAuthServerMock(
            hostname = "127.0.0.1",
            port = 8080,
        )
        // then
        assertThat(classUnderTest.hostname).isEqualTo("127.0.0.1")
        assertThat(classUnderTest.port).isEqualTo(8080)
    }

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
        val claims = mapOf(
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
        classUnderTest.start()
        // when
        val response = HttpTestClient.get(classUnderTest.getJwksUri())
        // then
        assertThat(response.statusCode()).isEqualTo(200)
        assertThat(response.contentType()).isEqualTo("application/json;charset=utf-8")
        // check jwks
        val jwks = response.fromJson(JWKS::class)
        assertThat(jwks.keys).hasSize(1)
        val jwk = jwks.keys.first()
        assertThat(jwk.kty).isEqualTo("RSA")
        assertThat(jwk.e).isEqualTo("AQAB")
        assertThat(jwk.use).isEqualTo("sig")
        assertThat(jwk.kid).matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}")
        assertThat(jwk.n).isNotNull
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
        assertThat(response.contentType()).isEqualTo("application/json;charset=utf-8")
        // check jwks
        val jwks = response.fromJson(JWKS::class)
        assertThat(jwks.keys).hasSize(1)
        val jwk = jwks.keys.first()
        assertThat(jwk.kty).isEqualTo("RSA")
        assertThat(jwk.e).isEqualTo("AQAB")
        assertThat(jwk.use).isEqualTo("sig")
        assertThat(jwk.kid).matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}")
        assertThat(jwk.n).isNotNull
        // clean up
        classUnderTest.shutdown()
    }
}
