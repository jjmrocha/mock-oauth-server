package net.uiqui.oauth.mock.http

import net.uiqui.oauth.mock.HttpTestClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.URI

internal class HttpServerTest {
    @Test
    fun `test getHost`() {
        // given
        val classUnderTest = HttpServer().apply { start() }
        // when
        val result = classUnderTest.getHost()
        // then
        assertThat(result).startsWith("http://localhost:")
        assertThat(result).isNotEqualTo("http://localhost:0")
        classUnderTest.stop()
    }

    @Test
    fun `test isRunning`() {
        // given
        val classUnderTest = HttpServer()
        // then
        assertThat(classUnderTest.isRunning()).isFalse
        // when - started
        classUnderTest.start()
        // then
        assertThat(classUnderTest.isRunning()).isTrue
        // when - stopped
        classUnderTest.stop()
        // then
        assertThat(classUnderTest.isRunning()).isFalse
    }

    @Test
    fun `test getPaths`() {
        // given
        val classUnderTest =
            HttpServer().apply {
                addHandler("/test200", TestRequestHandler(200, "OK"))
                addHandler("/other/test404", TestRequestHandler(404, "Not Found"))
                start()
            }
        // when
        val result = classUnderTest.getPaths()
        // then
        assertThat(result).containsExactlyInAnyOrder("/test200", "/other/test404")
        classUnderTest.stop()
    }

    @Test
    fun `test missing handler`() {
        // given
        val classUnderTest =
            HttpServer().apply {
                start()
            }
        // when
        val result = HttpTestClient.get(URI("${classUnderTest.getHost()}/missing"))
        // then
        assertThat(result.statusCode()).isEqualTo(404)
        classUnderTest.stop()
    }

    @Test
    fun `test execute handler`() {
        // given
        val classUnderTest =
            HttpServer().apply {
                addHandler("/test200", TestRequestHandler(200, "Ok"))
                addHandler("/other/test404", TestRequestHandler(404, "Not Found"))
                start()
            }
        // when
        val result = HttpTestClient.get(URI("${classUnderTest.getHost()}/other/test404"))
        // then
        assertThat(result.statusCode()).isEqualTo(404)
        classUnderTest.stop()
    }
}

internal class TestRequestHandler(private val status: Int, private val statusMessage: String) : RequestHandler {
    override fun handle(
        request: Request,
        response: Response,
    ) {
        response.setResponseCode(status, statusMessage)
    }
}
