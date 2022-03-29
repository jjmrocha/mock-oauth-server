package net.uiqui.oauth.mock

import jakarta.servlet.http.HttpServlet
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletHandler
import org.eclipse.jetty.servlet.ServletHolder
import java.net.InetSocketAddress

class HttpServer(inetAddress: InetSocketAddress) {
    private var servletHandler = ServletHandler().apply {
    }
    private val server = Server(inetAddress).apply {
        handler = servletHandler
    }

    fun start() {
        server.start()
    }

    fun stop() {
        server.stop()
    }

    fun addServlet(path: String, servlet: HttpServlet) {
        servletHandler.addServletWithMapping(ServletHolder(servlet), path)
    }

    fun getPaths() = servletHandler.servletMappings.flatMap {
        it.pathSpecs.asList()
    }

    fun isRunning() = server.isRunning

    fun getHost() = server.uri!!.toString()
}
