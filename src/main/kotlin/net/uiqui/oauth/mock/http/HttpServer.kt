package net.uiqui.oauth.mock.http

import net.uiqui.embedhttp.HttpServer
import net.uiqui.embedhttp.Router
import net.uiqui.embedhttp.api.HttpMethod
import net.uiqui.embedhttp.api.HttpRequestHandler

internal class HttpServer {
    private val handlers = mutableMapOf<String, HttpRequestHandler>()
    private val serverInstance = HttpServer.newInstance(0)

    fun start() {
        val router = Router.newRouter()

        handlers.forEach { (path, handler) ->
            router.withRoute(HttpMethod.GET, path, handler)
        }

        val result = serverInstance.start(router)
        check(result) { "Failed to start server" }
    }

    fun stop() {
        val result = serverInstance.stop()
        check(result) { "Failed to stop server" }
    }

    fun addHandler(
        path: String,
        handler: HttpRequestHandler,
    ) {
        check(!isRunning()) { "Server is running. Cannot add handler." }
        handlers[path] = handler
    }

    fun getPaths() = handlers.keys

    fun isRunning() = serverInstance.isRunning

    fun getHost(): String {
        val runningPort = serverInstance.instancePort
        check(runningPort != -1) { "Server is not running" }

        return "http://localhost:$runningPort"
    }
}
