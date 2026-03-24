package com.stviz.backend.price

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank

data class AssetPriceRequest(
    @field:NotBlank(message = "Symbol is required")
    val symbol: String,
    
    @field:DecimalMin(value = "0.01", message = "Price must be greater than 0")
    val currentPrice: Double
)

data class AssetPriceResponse(
    val id: Long,
    val portfolioId: Long,
    val symbol: String,
    val currentPrice: Double,
    val lastUpdated: String
)

fun AssetPriceEntity.toResponse() = AssetPriceResponse(
    id = id,
    portfolioId = portfolioId,
    symbol = symbol,
    currentPrice = currentPrice,
    lastUpdated = lastUpdated
)
