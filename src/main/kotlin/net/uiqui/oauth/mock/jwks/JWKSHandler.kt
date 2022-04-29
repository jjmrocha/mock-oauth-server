package net.uiqui.oauth.mock.jwks

import net.uiqui.oauth.mock.http.Request
import net.uiqui.oauth.mock.http.RequestHandler
import net.uiqui.oauth.mock.http.Response
import net.uiqui.oauth.mock.tools.JsonHelper

class JWKSHandler(private val jwks: JWKS) : RequestHandler {
    override fun handle(request: Request, response: Response) {
        response.setResponseCode(200, "OK")
        response.addHeader("Content-Type", "application/json;charset=utf-8")
        response.setBody(JsonHelper.toJson(jwks))
    }
}
