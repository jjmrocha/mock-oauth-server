package net.uiqui.oauth.mock.http

interface Request {
    fun getVersion(): String
    fun getUri(): String
    fun getMethod(): String
    fun getPath(): String
    fun getHeaders(): Map<String, String>
    fun getQueryParameters(): Map<String, String>
    fun getBody(): String?
}
