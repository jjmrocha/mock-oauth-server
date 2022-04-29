package net.uiqui.oauth.mock.http

interface RequestHandler {
    fun handle(request: Request, response: Response)
}
