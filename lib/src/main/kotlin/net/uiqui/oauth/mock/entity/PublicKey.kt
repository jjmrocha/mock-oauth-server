package net.uiqui.oauth.mock.entity

data class PublicKey(
    val kty: String,
    val e: String,
    val use: String,
    val kid: String,
    val n: String,
)
