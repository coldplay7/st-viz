package com.stviz.app.analytics

import kotlinx.serialization.Serializable

@Serializable
data class PortfolioAnalyticsResponse(
    val totalValue: Double,
    val totalCost: Double,
    val realizedPnl: Double,
    val unrealizedPnl: Double,
    val roi: Double,
    val sectorAllocation: List<SectorAllocation>,
    val positions: List<PositionMetrics>
)

@Serializable
data class SectorAllocation(
    val sector: String,
    val value: Double,
    val percentage: Double
)

@Serializable
data class PositionMetrics(
    val symbol: String,
    val sector: String,
    val quantityHeld: Double,
    val averageBuyPrice: Double,
    val currentPrice: Double,
    val totalCost: Double,
    val currentValue: Double,
    val unrealizedPnl: Double,
    val roi: Double
)
