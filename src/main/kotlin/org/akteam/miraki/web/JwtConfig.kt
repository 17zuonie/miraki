package org.akteam.miraki.web

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import org.akteam.miraki.BotVariables
import org.akteam.miraki.model.BotUser
import java.util.*

object JwtConfig {
    private val secret = BotVariables.cfg.jwtSecret
    private const val issuer = "aki"
    private const val audience = "happy"
    const val realm = "Aki Server"
    private val algorithm = Algorithm.HMAC256(secret)

    val verifier: JWTVerifier = JWT
            .require(algorithm)
            .withIssuer(issuer)
            .withAudience(audience)
            .acceptExpiresAt(3)
            .build()

    fun makeToken(user: BotUser): String = JWT.create()
            .withSubject("Authentication")
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("qq", user.qq)
            .withExpiresAt(getExpiration())
            .sign(algorithm)

    private fun getExpiration() = Date(System.currentTimeMillis() + 15 * 60 * 1000)
}