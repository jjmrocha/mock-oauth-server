package net.uiqui.oauth.mock.http

internal interface RequestHandler {
    fun handle(
        request: Request,
        response: Response,
    )
}
