package com.stviz.app.portfolio

import com.stviz.app.common.dto.ApiResponse
import com.stviz.app.network.NetworkClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable

class PortfolioRepository {
    private val client = NetworkClient.client

    suspend fun getPortfolios(): Result<List<PortfolioResponse>> = runCatching {
        val response: ApiResponse<List<PortfolioResponse>> = client.get("/api/portfolios").body()
        response.data ?: throw Exception(response.error ?: "Failed to fetch portfolios")
    }

    suspend fun createPortfolio(request: CreatePortfolioRequest): Result<PortfolioResponse> = runCatching {
        val response: ApiResponse<PortfolioResponse> = client.post("/api/portfolios") {
            setBody(request)
        }.body()
        response.data ?: throw Exception(response.error ?: "Failed to create portfolio")
    }

    suspend fun deletePortfolio(id: Long): Result<Unit> = runCatching {
        client.delete("/api/portfolios/$id")
    }
}
