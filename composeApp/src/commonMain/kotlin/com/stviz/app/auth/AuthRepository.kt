package com.stviz.app.auth

import com.stviz.app.common.dto.ApiResponse
import com.stviz.app.network.NetworkClient
import com.stviz.app.network.TokenManager
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val username: String,
    val email: String
)

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class RegisterRequest(val username: String, val email: String, val password: String)

class AuthRepository(private val tokenManager: TokenManager = TokenManager()) {
    
    private val client = NetworkClient.client

    suspend fun login(request: LoginRequest): Result<AuthResponse> = runCatching {
        val response: ApiResponse<AuthResponse> = client.post("/api/auth/login") {
            setBody(request)
        }.body()
        
        val authData = response.data ?: throw Exception(response.error ?: "Unknown login error")
        tokenManager.saveToken(authData.token)
        authData
    }

    suspend fun register(request: RegisterRequest): Result<AuthResponse> = runCatching {
        val response: ApiResponse<AuthResponse> = client.post("/api/auth/register") {
            setBody(request)
        }.body()
        
        val authData = response.data ?: throw Exception(response.error ?: "Unknown registration error")
        tokenManager.saveToken(authData.token)
        authData
    }

    fun logout() {
        tokenManager.clearToken()
    }

    fun isLoggedIn(): Boolean = tokenManager.hasToken()
}
