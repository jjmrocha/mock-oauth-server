package net.uiqui.oauth.mock.boundary

import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.uiqui.oauth.mock.control.JsonHelper
import net.uiqui.oauth.mock.entity.JWKS

class KeysServlet(private val jwks: JWKS) : HttpServlet() {
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.status = 200
        resp.contentType = "application/json"
        resp.characterEncoding = "UTF-8"

        resp.writer.apply {
            print(JsonHelper.toJson(jwks))
            flush()
        }
    }
}
