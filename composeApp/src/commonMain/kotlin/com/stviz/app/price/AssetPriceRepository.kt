package com.stviz.app.price

import com.stviz.app.common.dto.ApiResponse
import com.stviz.app.network.NetworkClient
import io.ktor.client.call.*
import io.ktor.client.request.*

class AssetPriceRepository {
    private val client = NetworkClient.client

    suspend fun updatePrice(request: AssetPriceRequest): Result<AssetPriceResponse> = runCatching {
        val response: ApiResponse<AssetPriceResponse> = client.post("/api/prices") {
            setBody(request)
        }.body()
        response.data ?: throw Exception(response.error ?: "Failed to update price")
    }

    suspend fun getPrice(symbol: String): Result<AssetPriceResponse> = runCatching {
        val response: ApiResponse<AssetPriceResponse> = client.get("/api/prices/$symbol").body()
        response.data ?: throw Exception(response.error ?: "Failed to fetch price")
    }
}
