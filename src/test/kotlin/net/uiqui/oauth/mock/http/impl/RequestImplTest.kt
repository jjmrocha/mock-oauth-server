package net.uiqui.oauth.mock.http.impl

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream

internal class RequestImplTest {
    @Test
    fun `parse post`() {
        // given
        val httpRequest = """
        |POST /api/resource HTTP/1.1
        |Content-Type: application/json
        |Content-Length: 19
        |
        |{ "key" : "value" }
        """.trimMargin()
        // when
        val result = RequestImpl.parse((ByteArrayInputStream(httpRequest.toByteArray())))
        // then
        assertThat(result.getUri()).isEqualTo("/api/resource")
        assertThat(result.getPath()).isEqualTo("/api/resource")
        assertThat(result.getQueryParameters()).isEmpty()
        assertThat(result.getVersion()).isEqualTo("HTTP/1.1")
        assertThat(result.getHeaders().size).isEqualTo(2)
        assertThat(result.getHeaders()).containsEntry("Content-Type", "application/json")
        assertThat(result.getBody()).isEqualTo("""{ "key" : "value" }""")
    }

    @Test
    fun `parse get`() {
        // given
        val httpRequest = """
        |GET /api/resource?key1=value1&key2=value2 HTTP/1.1
        """.trimMargin()
        // when
        val result = RequestImpl.parse((ByteArrayInputStream(httpRequest.toByteArray())))
        // then
        assertThat(result.getUri()).isEqualTo("/api/resource?key1=value1&key2=value2")
        assertThat(result.getPath()).isEqualTo("/api/resource")
        assertThat(result.getQueryParameters().size).isEqualTo(2)
        assertThat(result.getQueryParameters()).containsEntry("key1", "value1")
        assertThat(result.getQueryParameters()).containsEntry("key2", "value2")
        assertThat(result.getVersion()).isEqualTo("HTTP/1.1")
        assertThat(result.getHeaders().size).isEqualTo(0)
    }
}
