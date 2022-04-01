package net.uiqui.oauth.mock.control

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.URI

internal class JWTGeneratorTest {
    @Test
    fun `test jwt generation`() {
        // given
        val classUnderTest = JWTGenerator()
        val claims = mapOf(
            "aud" to "audience",
            "iss" to "https://issuer/",
            "custom-float" to 123.45,
            "custom-list" to listOf("value1", "value2")
        )
        // when - encoded
        val jwt = classUnderTest.generate(URI("http://jwks/"), claims)
        // then
        assertThat(jwt).isInstanceOf(String::class.java)
        // when - decoded
        val result = classUnderTest.parseJwt(jwt)
        // then
        assertThat(result["aud"] as List<*>).contains("audience")
        assertThat(result["iss"]).isEqualTo("https://issuer/")
        assertThat(result["custom-float"]).isEqualTo(123.45)
        assertThat(result["custom-list"] as List<*>).containsExactly("value1", "value2")
    }

    @Test
    fun `test JWKS creation`() {
        // given
        val classUnderTest = JWTGenerator()
        // when
        val result = classUnderTest.getJWKS()
        // then
        assertThat(result.keys).hasSize(1)
        val jwk = result.keys.first()
        assertThat(jwk.kty).isEqualTo("RSA")
        assertThat(jwk.e).isEqualTo("AQAB")
        assertThat(jwk.use).isEqualTo("sig")
        assertThat(jwk.kid).matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}")
        assertThat(jwk.n).isNotNull
    }
}
