package net.uiqui.oauth.mock.http

import net.uiqui.oauth.mock.http.impl.RequestImpl
import net.uiqui.oauth.mock.http.impl.ResponseImpl
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class HttpServer {
    private val handlers = mutableMapOf<String, RequestHandler>()
    private val running = AtomicBoolean(false)
    private val localPort = AtomicInteger(0)
    private var thread: Thread? = null

    fun start() {
        val bootingLatch = CountDownLatch(1)

        thread = Thread {
            ServerSocket(0, 10).use { serverSocket ->
                localPort.set(serverSocket.localPort)
                running.set(true)
                bootingLatch.countDown()

                while (running.get()) serverSocket.accept().use { handleRequest(it) }
            }
        }.apply {
            isDaemon = true
            start()
        }

        bootingLatch.await()
    }

    private fun handleRequest(clientSocket: Socket) {
        val request = RequestImpl.parse(clientSocket.getInputStream())
        val response = ResponseImpl(request.getVersion())
        val handler = handlers[request.getPath()]

        if (handler != null) {
            handler.handle(request, response)
        } else {
            response.setResponseCode(404, "Not Found")
        }

        response.sendResponse(clientSocket.getOutputStream())
    }

    fun stop() {
        running.set(false)
        thread!!.interrupt()
    }

    fun addHandler(path: String, handler: RequestHandler) {
        handlers[path] = handler
    }

    fun getPaths() = handlers.keys

    fun isRunning() = running.get()

    fun getHost() =
        if (isRunning()) "http://localhost:${localPort.get()}" else null
}
