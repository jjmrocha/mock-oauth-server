package net.uiqui.oauth.mock

import net.uiqui.oauth.mock.tools.JsonHelper
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers
import kotlin.reflect.KClass

object HttpTestClient {
    private val httpClient = HttpClient.newHttpClient()

    fun get(endpoint: URI): HttpResponse<String> {
        val request = HttpRequest.newBuilder()
            .uri(endpoint)
            .header("Accept", "application/json")
            .GET()
            .build()
        return httpClient.send(request, BodyHandlers.ofString())
    }
}

fun HttpResponse<*>.contentType(): String? {
    return this.headers().firstValue("content-type").orElse(null)
}

fun <T : Any> HttpResponse<String>.fromJson(type: KClass<T>): T {
    return JsonHelper.fromJson(this.body(), type)
}
