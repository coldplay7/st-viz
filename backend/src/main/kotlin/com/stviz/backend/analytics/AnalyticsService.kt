package com.stviz.backend.analytics

import com.stviz.backend.common.exception.NotFoundException
import com.stviz.backend.portfolio.PortfolioRepository
import com.stviz.backend.price.AssetPriceRepository
import com.stviz.backend.transaction.TransactionRepository
import com.stviz.backend.transaction.TransactionType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AnalyticsService(
    private val portfolioRepository: PortfolioRepository,
    private val transactionRepository: TransactionRepository,
    private val assetPriceRepository: AssetPriceRepository
) {

    @Transactional(readOnly = true)
    fun getPortfolioAnalytics(userId: Long, portfolioId: Long): PortfolioAnalyticsResponse {
        val portfolio = portfolioRepository.findByIdAndUserId(portfolioId, userId)
            ?: throw NotFoundException("Portfolio not found or access denied")

        val transactions = transactionRepository.findAllByPortfolioId(portfolioId, org.springframework.data.domain.Pageable.unpaged()).content
        val prices = assetPriceRepository.findAllByPortfolioId(portfolioId).associateBy { it.symbol }

        val symbols = transactions.map { it.symbol }.distinct()
        val positions = symbols.map { symbol ->
            calculatePositionMetrics(symbol, transactions.filter { it.symbol == symbol }, prices[symbol]?.currentPrice ?: 0.0)
        }.filter { it.quantityHeld > 0 || it.unrealizedPnl != 0.0 } // Keep closed positions if they impact PnL? Actually, unrealized is 0 for closed.

        val totalValue = positions.sumOf { it.currentValue }
        val totalCost = positions.sumOf { it.totalCost }
        val unrealizedPnl = positions.sumOf { it.unrealizedPnl }
        
        // Realized PnL calculation
        val realizedPnl = calculateRealizedPnl(transactions)

        val roi = if (totalCost > 0) ( (totalValue + realizedPnl - totalCost) / totalCost ) * 100 else 0.0

        val sectorMap = positions.groupBy { it.sector }
        val sectorAllocation = sectorMap.map { (sector, posList) ->
            val sectorValue = posList.sumOf { it.currentValue }
            SectorAllocation(
                sector = sector,
                value = sectorValue,
                percentage = if (totalValue > 0) (sectorValue / totalValue) * 100 else 0.0
            )
        }.sortedByDescending { it.percentage }

        return PortfolioAnalyticsResponse(
            totalValue = totalValue,
            totalCost = totalCost,
            realizedPnl = realizedPnl,
            unrealizedPnl = unrealizedPnl,
            roi = roi,
            sectorAllocation = sectorAllocation,
            positions = positions.sortedByDescending { it.currentValue }
        )
    }

    private fun calculatePositionMetrics(symbol: String, txs: List<com.stviz.backend.transaction.TransactionEntity>, currentPrice: Double): PositionMetrics {
        var qty = 0.0
        var totalBuyCost = 0.0
        var buyCount = 0.0
        val sector = txs.firstOrNull()?.sector ?: "Unknown"

        // Using FIFO or Average Cost? Let's use Average Cost for simplicity as common in retail trackers.
        for (tx in txs) {
            if (tx.type == TransactionType.BUY) {
                totalBuyCost += (tx.quantity * tx.price)
                qty += tx.quantity
                buyCount += tx.quantity
            } else {
                // When selling, we reduce the quantity but the "Average Cost" of remaining shares stays same.
                // However, for totalCost of current position, it's avgPrice * currentQty.
                qty -= tx.quantity
            }
        }

        val avgPrice = if (buyCount > 0) totalBuyCost / buyCount else 0.0
        val currentTotalCost = qty * avgPrice
        val currentValue = qty * currentPrice
        val unrealizedPnl = currentValue - currentTotalCost
        val roi = if (currentTotalCost > 0) (unrealizedPnl / currentTotalCost) * 100 else 0.0

        return PositionMetrics(
            symbol = symbol,
            sector = sector,
            quantityHeld = qty,
            averageBuyPrice = avgPrice,
            currentPrice = currentPrice,
            totalCost = currentTotalCost,
            currentValue = currentValue,
            unrealizedPnl = unrealizedPnl,
            roi = roi
        )
    }

    private fun calculateRealizedPnl(txs: List<com.stviz.backend.transaction.TransactionEntity>): Double {
        // Realized PnL = (Sell Price - Avg Buy Price) * Qty Sold
        // We need to track average buy price dynamically.
        var totalRealizedPnl = 0.0
        var currentQty = 0.0
        var totalBuyCost = 0.0
        var buyCount = 0.0

        for (tx in txs.sortedBy { it.transactionDate }) {
            if (tx.type == TransactionType.BUY) {
                totalBuyCost += (tx.quantity * tx.price)
                currentQty += tx.quantity
                buyCount += tx.quantity
            } else {
                val avgBuyPrice = if (buyCount > 0) totalBuyCost / buyCount else 0.0
                totalRealizedPnl += (tx.price - avgBuyPrice) * tx.quantity
                currentQty -= tx.quantity
                // Note: In a true Average Cost model, selling doesn't change the avg cost of remaining shares.
            }
        }
        return totalRealizedPnl
    }
}
