package net.uiqui.oauth.mock

import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.URL

internal class HttpServerTest {
    @Test
    fun `test getHost when port is defined`() {
        // given
        val inetAddress = InetSocketAddress("localhost", 8080)
        val classUnderTest = HttpServer(inetAddress).apply { start() }
        // when
        val result = classUnderTest.getHost()
        // then
        assertThat(result).isEqualTo("http://localhost:8080/")
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
        assertThat(result).isNotEqualTo("http://localhost:0/")
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
        val http: HttpURLConnection = URL("http://localhost:8080/other/test404").openConnection() as HttpURLConnection
        http.connect()
        val result = http.responseCode
        // then
        assertThat(result).isEqualTo(404)
        classUnderTest.stop()
    }
}

class TestServlet(private val status: Int) : HttpServlet() {
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.status = status
    }
}
