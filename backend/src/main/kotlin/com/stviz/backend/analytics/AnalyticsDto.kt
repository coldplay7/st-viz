package com.stviz.backend.analytics

import com.stviz.backend.transaction.TransactionType

data class PortfolioAnalyticsResponse(
    val totalValue: Double,
    val totalCost: Double,
    val realizedPnl: Double,
    val unrealizedPnl: Double,
    val roi: Double,
    val sectorAllocation: List<SectorAllocation>,
    val positions: List<PositionMetrics>
)

data class SectorAllocation(
    val sector: String,
    val value: Double,
    val percentage: Double
)

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
