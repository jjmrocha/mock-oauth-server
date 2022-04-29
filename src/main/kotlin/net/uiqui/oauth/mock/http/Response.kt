package net.uiqui.oauth.mock.http

interface Response {
    fun setResponseCode(statusCode: Int, statusMessage: String)
    fun addHeader(headerName: String, headerValue: String)
    fun setBody(body: String)
}
