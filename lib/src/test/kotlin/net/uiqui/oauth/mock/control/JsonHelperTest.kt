package net.uiqui.oauth.mock.control

import net.uiqui.oauth.mock.entity.JWKS
import net.uiqui.oauth.mock.entity.PublicKey
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class JsonHelperTest {
    @Test
    fun `test json serialization`() {
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
        // when
        val json = JsonHelper.toJson(jwks)
        val result = JsonHelper.fromJson(json, JWKS::class)
        // then
        assertThat(json).isEqualTo("""{"keys":[{"kty":"kty value","e":"kty value","use":"kty value","kid":"kty value","n":"kty value"}]}""")
        assertThat(result).isEqualTo(jwks)
    }
}
