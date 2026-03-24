package com.stviz.app.price

import kotlinx.serialization.Serializable

@Serializable
data class AssetPriceResponse(
    val symbol: String,
    val currentPrice: Double,
    val lastUpdated: String
)

@Serializable
data class AssetPriceRequest(
    val symbol: String,
    val currentPrice: Double
)
