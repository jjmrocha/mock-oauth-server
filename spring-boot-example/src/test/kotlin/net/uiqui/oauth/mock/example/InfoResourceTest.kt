package net.uiqui.oauth.mock.example

import io.mockk.every
import io.mockk.mockk
import net.uiqui.oauth.mock.OAuthServerMock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = [InfoResourceTest.TestSettings::class])
@AutoConfigureMockMvc
internal class InfoResourceTest {
    companion object {
        val mockedAuthenticationConfig = mockk<AuthenticationConfig>()
    }

    @TestConfiguration
    class TestSettings {
        @Bean("authenticationConfig")
        fun getAuthenticationConfig() = mockedAuthenticationConfig
    }

    private val mockedOauthServer = OAuthServerMock()

    @BeforeEach
    fun init() {
        mockedOauthServer.start()
        every { mockedAuthenticationConfig.jwksEndpoint } returns mockedOauthServer.getJwksUri().toString()
        every { mockedAuthenticationConfig.oauthIssuer } returns "OAuth-Server-Mock"
        every { mockedAuthenticationConfig.oauthAudience } returns "this-unit-test"
    }

    @AfterEach
    fun cleanUp() {
        mockedOauthServer.shutdown()
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `test runtimeInfo without access token`() {
        // when
        val response = mockMvc.perform(
            get("/info/runtime")
        ).andReturn().response
        // then
        assertThat(response.status).isEqualTo(401)
    }

    @Test
    fun `test runtimeInfo with invalid access token`() {
        // when
        val response = mockMvc.perform(
            get("/info/runtime")
                .header(AUTHORIZATION, "Bearer invalid")
        ).andReturn().response
        // then
        assertThat(response.status).isEqualTo(401)
    }

    @Test
    fun `test runtimeInfo with valid access token`() {
        // given
        val requiredClaims = mapOf(
            "iss" to "OAuth-Server-Mock",
            "aud" to "this-unit-test",
            "appid" to "ad4fc666-c793-11ec-9d64-0242ac120002"
        )
        val jwtToken = mockedOauthServer.generateJWT(requiredClaims)
        // when
        val response = mockMvc.perform(
            get("/info/runtime")
                .header(AUTHORIZATION, "Bearer $jwtToken")
        ).andReturn().response
        // then
        assertThat(response.status).isEqualTo(200)
    }
}
