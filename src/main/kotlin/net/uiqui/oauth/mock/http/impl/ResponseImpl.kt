package net.uiqui.oauth.mock.http.impl

import net.uiqui.oauth.mock.http.Response
import java.io.OutputStream

internal class ResponseImpl(private val version: String) : Response {
    private var statusCode = 200
    private var statusMessage = "OK"
    private val headers = mutableMapOf<String, String>()
    private var body: String? = null

    override fun setResponseCode(
        statusCode: Int,
        statusMessage: String,
    ) {
        this.statusCode = statusCode
        this.statusMessage = statusMessage
    }

    override fun addHeader(
        headerName: String,
        headerValue: String,
    ) {
        headers[headerName] = headerValue
    }

    override fun setBody(body: String) {
        headers["Content-Length"] = body.length.toString()
        this.body = body
    }

    fun sendResponse(outputStream: OutputStream) {
        headers["Connection"] = "Close"

        outputStream.write("$version $statusCode $statusMessage\r\n".toByteArray())

        headers.forEach { (headerName, headerValue) ->
            outputStream.write("$headerName: $headerValue\r\n".toByteArray())
        }

        outputStream.write("\r\n".toByteArray())

        if (body != null) {
            outputStream.write(body!!.toByteArray())
        }

        outputStream.flush()
    }
}
