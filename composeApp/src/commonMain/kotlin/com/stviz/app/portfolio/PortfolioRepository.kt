package com.stviz.app.portfolio

import com.stviz.app.common.dto.ApiResponse
import com.stviz.app.network.NetworkClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable

import com.stviz.app.analytics.PortfolioAnalyticsResponse
import com.stviz.app.transaction.PageResponse
import com.stviz.app.transaction.TransactionResponse

class PortfolioRepository {
    private val client = NetworkClient.client

    suspend fun getPortfolios(): Result<List<PortfolioResponse>> = runCatching {
        val response: ApiResponse<List<PortfolioResponse>> = client.get("/api/portfolios").body()
        response.data ?: throw Exception(response.error ?: "Failed to fetch portfolios")
    }

    suspend fun getAnalytics(portfolioId: Long): Result<PortfolioAnalyticsResponse> = runCatching {
        val response: ApiResponse<PortfolioAnalyticsResponse> = client.get("/api/portfolios/$portfolioId/analytics").body()
        response.data ?: throw Exception(response.error ?: "Failed to fetch analytics")
    }

    suspend fun getTransactions(portfolioId: Long): Result<List<TransactionResponse>> = runCatching {
        val response: ApiResponse<PageResponse<TransactionResponse>> = client.get("/api/portfolios/$portfolioId/transactions").body()
        response.data?.content ?: throw Exception(response.error ?: "Failed to fetch transactions")
    }

    suspend fun createTransaction(portfolioId: Long, request: com.stviz.app.transaction.TransactionRequest): Result<TransactionResponse> = runCatching {
        val response: ApiResponse<TransactionResponse> = client.post("/api/portfolios/$portfolioId/transactions") {
            setBody(request)
        }.body()
        response.data ?: throw Exception(response.error ?: "Failed to create transaction")
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
