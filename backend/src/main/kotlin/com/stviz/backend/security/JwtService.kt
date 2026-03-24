package com.stviz.backend.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService(
    @Value("\${jwt.secret}") private val jwtSecret: String,
    @Value("\${jwt.expirationMs}") private val jwtExpirationMs: Long
) {
    private val key: SecretKey
        get() = Keys.hmacShaKeyFor(jwtSecret.toByteArray())

    fun generateToken(id: Long, email: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtExpirationMs)

        return Jwts.builder()
            .subject(id.toString())
            .claim("email", email)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact()
    }

    fun getUserIdFromToken(token: String): Long {
        val claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
        return claims.payload.subject.toLong()
    }

    fun validateToken(authToken: String): Boolean {
        return try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(authToken)
            true
        } catch (ex: Exception) {
            false
        }
    }
}
