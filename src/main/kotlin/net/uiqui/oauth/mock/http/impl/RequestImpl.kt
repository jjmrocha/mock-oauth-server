package net.uiqui.oauth.mock.http.impl

import net.uiqui.oauth.mock.http.Request
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class RequestImpl : Request {
    private var version: String? = null
    private var method: String? = null
    private var uri: String? = null
    private val headers = mutableMapOf<String, String>()
    private var body: String? = null

    override fun getVersion(): String {
        return version!!
    }

    override fun getUri(): String {
        return uri!!
    }

    override fun getMethod(): String {
        return method!!
    }

    override fun getPath(): String {
        return getUri().substringBefore("?")
    }

    override fun getHeaders(): Map<String, String> {
        return headers
    }

    override fun getQueryParameters(): Map<String, String> {
        val queryParameters = getUri().substringAfter("?", "")

        if (queryParameters.isNotEmpty()) {
            return queryParameters.split("&").associate { parameter ->
                val parameterName = parameter.substringBefore("=")
                val parameterValue = parameter.substringAfter("=")
                parameterName to parameterValue
            }
        }

        return emptyMap()
    }

    override fun getBody(): String? {
        return body
    }

    companion object {
        fun parse(inputStream: InputStream): Request =
            RequestImpl().also { request ->
                BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)).also { reader ->
                    decodeRequestLine(reader, request)
                    decodeRequestHeader(reader, request)
                    decodeRequestBody(reader, request)
                }
            }

        private fun decodeRequestLine(reader: BufferedReader, request: RequestImpl) {
            val parts = reader.readLine().split(" ")
            request.method = parts[0]
            request.uri = parts[1]
            request.version = parts[2]
        }

        private fun decodeRequestHeader(reader: BufferedReader, request: RequestImpl) {
            while (true) {
                val line = reader.readLine()
                if (line.isNullOrBlank()) break
                val headerName = line.substringBefore(":")
                val headerValue = line.substringAfter(":").trim()
                request.headers[headerName] = headerValue
            }
        }

        private fun decodeRequestBody(reader: BufferedReader, request: RequestImpl) {
            val contentLen = request.headers.getOrDefault("Content-Length", "0").toInt()

            if (contentLen > 0) {
                val message = CharArray(contentLen)
                reader.read(message)
                request.body = String(message)
            }
        }
    }
}
