package net.uiqui.oauth.mock.http

internal interface Response {
    fun setResponseCode(
        statusCode: Int,
        statusMessage: String,
    )

    fun addHeader(
        headerName: String,
        headerValue: String,
    )

    fun setBody(body: String)
}
