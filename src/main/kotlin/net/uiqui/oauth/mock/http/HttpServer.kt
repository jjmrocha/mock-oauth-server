package net.uiqui.oauth.mock.http

import net.uiqui.oauth.mock.http.impl.RequestImpl
import net.uiqui.oauth.mock.http.impl.ResponseImpl
import net.uiqui.oauth.mock.tools.Holder
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.util.concurrent.CountDownLatch

class HttpServer {
    private val handlers = mutableMapOf<String, RequestHandler>()
    private val serverInstance = ServerInstance()

    fun start() {
        serverInstance.start(handlers)
    }

    fun stop() {
        serverInstance.stop()
    }

    fun addHandler(path: String, handler: RequestHandler) {
        handlers[path] = handler
    }

    fun getPaths() = handlers.keys

    fun isRunning() = serverInstance.isRunning()

    fun getHost() = "http://localhost:${serverInstance.getPort()}"
}

private class ServerInstance {
    private val bootingLatch = Holder(CountDownLatch(1))
    private val stoppingLatch = Holder(CountDownLatch(1))
    private val starting = Holder(false)
    private val running = Holder(false)
    private val stopping = Holder(false)
    private var socket: ServerSocket? = null

    fun start(handlers: Map<String, RequestHandler>) {
        fun handleRequest(clientSocket: Socket) {
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

        if (running.value) return

        if (stopping.value) stoppingLatch.value.await()

        if (!starting.value) {
            starting.value = true

            Thread {
                ServerSocket(0, 10).use { serverSocket ->
                    socket = serverSocket
                    running.value = true
                    starting.value = false
                    stopping.value = false
                    stoppingLatch.value = CountDownLatch(1)
                    bootingLatch.value.countDown()

                    while (!stopping.value)
                        try {
                            serverSocket.accept().use { handleRequest(it) }
                        } catch (e: SocketException) {
                            if (!socket!!.isClosed) throw e
                        }

                    running.value = false
                    stoppingLatch.value.countDown()
                }
            }.apply {
                isDaemon = true
                start()
            }
        }

        bootingLatch.value.await()
    }

    fun stop() {
        if (!running.value) return

        if (starting.value) bootingLatch.value.await()

        if (!stopping.value) {
            stopping.value = true
            socket!!.close()
        }

        stoppingLatch.value.await()
        bootingLatch.value = CountDownLatch(1)
    }

    fun isRunning() = running.value

    fun getPort() =
        if (running.value) socket!!.localPort else throw IllegalStateException("host port is only available after starting")
}
