package net.uiqui.oauth.mock.jwks

data class PublicKey(
    val kty: String,
    val e: String,
    val use: String,
    val kid: String,
    val n: String,
)
