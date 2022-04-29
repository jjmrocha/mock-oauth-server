package net.uiqui.oauth.mock.http.impl

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream

internal class ResponseImplTest {
    @Test
    fun `test basic response`() {
        // given
        val classUnderTest = ResponseImpl("HTTP/1.1")
        // when
        classUnderTest.setResponseCode(404, "Not Found")

        val result = ByteArrayOutputStream()
        classUnderTest.sendResponse(result)
        // then
        assertThat(String(result.toByteArray())).isEqualTo(
            """
            |HTTP/1.1 404 Not Found
            |Connection: Close
            |
            |
            """.trimMargin().replace("\n", "\r\n")
        )
    }

    @Test
    fun `test full response`() {
        // given
        val classUnderTest = ResponseImpl("HTTP/1.1")
        // when
        classUnderTest.setResponseCode(200, "OK")
        classUnderTest.addHeader("Content-Type", "application/json;charset=utf-8")
        classUnderTest.setBody("""{ "key" : "value" }""")

        val result = ByteArrayOutputStream()
        classUnderTest.sendResponse(result)
        // then
        assertThat(String(result.toByteArray())).isEqualTo(
            """
            |HTTP/1.1 200 OK
            |Content-Type: application/json;charset=utf-8
            |Content-Length: 19
            |Connection: Close
            |
            |{ "key" : "value" }
            """.trimMargin().replace("\n", "\r\n")
        )
    }
}
