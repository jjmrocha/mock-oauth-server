package net.uiqui.oauth.mock.jwks

import net.uiqui.embedhttp.api.ContentType
import net.uiqui.embedhttp.api.HttpRequest
import net.uiqui.embedhttp.api.HttpRequestHandler
import net.uiqui.embedhttp.api.HttpResponse
import net.uiqui.oauth.mock.tools.JsonHelper

internal class JWKSHandler(private val jwks: JWKS) : HttpRequestHandler {
    override fun handle(request: HttpRequest): HttpResponse {
        return HttpResponse.ok()
            .setBody(ContentType.APPLICATION_JSON, JsonHelper.toJson(jwks))
    }
}
