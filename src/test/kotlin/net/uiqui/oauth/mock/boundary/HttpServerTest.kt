package net.uiqui.oauth.mock.boundary

import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.uiqui.oauth.mock.HttpTestClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.InetSocketAddress
import java.net.URI

internal class HttpServerTest {
    @Test
    fun `test getHost when port is defined`() {
        // given
        val inetAddress = InetSocketAddress("localhost", 8080)
        val classUnderTest = HttpServer(inetAddress).apply { start() }
        // when
        val result = classUnderTest.getHost()
        // then
        assertThat(result).isEqualTo("http://localhost:8080")
        classUnderTest.stop()
    }

    @Test
    fun `test getHost when port is not defined`() {
        // given
        val inetAddress = InetSocketAddress("localhost", 0)
        val classUnderTest = HttpServer(inetAddress).apply { start() }
        // when
        val result = classUnderTest.getHost()
        // then
        assertThat(result).isNotEqualTo("http://localhost:0")
        classUnderTest.stop()
    }

    @Test
    fun `test isRunning`() {
        // given
        val inetAddress = InetSocketAddress("localhost", 0)
        val classUnderTest = HttpServer(inetAddress)
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
        val inetAddress = InetSocketAddress("localhost", 0)
        val classUnderTest = HttpServer(inetAddress).apply {
            addServlet("/test200", TestServlet(200))
            addServlet("/other/test404", TestServlet(404))
            start()
        }
        // when
        val result = classUnderTest.getPaths()
        // then
        assertThat(result).containsExactlyInAnyOrder("/test200", "/other/test404", "/")
        classUnderTest.stop()
    }

    @Test
    fun `test servlets`() {
        // given
        val inetAddress = InetSocketAddress("localhost", 8080)
        val classUnderTest = HttpServer(inetAddress).apply {
            addServlet("/test200", TestServlet(200))
            addServlet("/other/test404", TestServlet(404))
            start()
        }
        // when
        val result = HttpTestClient.get(URI("http://localhost:8080/other/test404"))
        // then
        assertThat(result.statusCode()).isEqualTo(404)
        classUnderTest.stop()
    }
}

class TestServlet(private val status: Int) : HttpServlet() {
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.status = status
    }
}
