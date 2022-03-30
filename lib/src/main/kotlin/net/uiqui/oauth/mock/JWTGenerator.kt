package net.uiqui.oauth.mock

import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jose.jwk.KeyUse
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import java.util.Date
import java.util.UUID

private const val JWT_TTL = 30 * 60000

class JWTGenerator {
    private val rsaKey = RSAKeyGenerator(2048)
        .keyUse(KeyUse.SIGNATURE)
        .keyID(UUID.randomUUID().toString())
        .generate()
    private val jwtHeader = JWSHeader.Builder(JWSAlgorithm.RS256)
        .keyID(rsaKey.keyID)
        .build()
    private val jwtVerifier = RSASSAVerifier(rsaKey.toRSAPublicKey())

    fun getJWKS(): JWKS {
        val publicKey = rsaKey.toPublicJWK().toJSONObject()
        return JWKS(
            keys = listOf(
                JWK(
                    kty = publicKey["kty"].toString(),
                    e = publicKey["e"].toString(),
                    use = publicKey["use"].toString(),
                    kid = publicKey["kid"].toString(),
                    n = publicKey["n"].toString(),
                )
            )
        )
    }

    fun generate(claims: Map<String, Any>): String {
        val jwtClaimSet = toJWTClaimsSet(claims)
        return generateJWT(jwtClaimSet)
    }

    fun parseJwt(jwt: String): Map<String, Any> {
        val signedJWT = SignedJWT.parse(jwt)
        if (!signedJWT.verify(jwtVerifier)) throw JOSEException("Invalid signature")
        return signedJWT.jwtClaimsSet.claims
    }

    private fun toJWTClaimsSet(claims: Map<String, Any>): JWTClaimsSet {
        val now = Date()

        val claimBuilder = JWTClaimsSet.Builder()
            .notBeforeTime(now)
            .expirationTime(Date(now.time + JWT_TTL))

        claims.forEach { (claimName, value) -> claimBuilder.claim(claimName, value) }

        return claimBuilder.build()
    }

    private fun generateJWT(claims: JWTClaimsSet): String {
        val signedJWT = SignedJWT(jwtHeader, claims)
        val signer = RSASSASigner(rsaKey)
        signedJWT.sign(signer)
        return signedJWT.serialize()
    }
}

data class JWK(
    val kty: String,
    val e: String,
    val use: String,
    val kid: String,
    val n: String,
)

data class JWKS(
    val keys: Collection<JWK>
)
