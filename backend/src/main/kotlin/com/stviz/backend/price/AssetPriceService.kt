package com.stviz.backend.price

import com.stviz.backend.common.exception.NotFoundException
import com.stviz.backend.portfolio.PortfolioRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class AssetPriceService(
    private val assetPriceRepository: AssetPriceRepository,
    private val portfolioRepository: PortfolioRepository
) {

    @Transactional(readOnly = true)
    fun getPrices(userId: Long, portfolioId: Long): List<AssetPriceResponse> {
        verifyPortfolioOwnership(userId, portfolioId)
        return assetPriceRepository.findAllByPortfolioId(portfolioId).map { it.toResponse() }
    }

    @Transactional
    fun upsertPrice(userId: Long, portfolioId: Long, request: AssetPriceRequest): AssetPriceResponse {
        verifyPortfolioOwnership(userId, portfolioId)

        val existing = assetPriceRepository.findByPortfolioIdAndSymbol(portfolioId, request.symbol)
        val entity = if (existing != null) {
            existing.currentPrice = request.currentPrice
            existing.lastUpdated = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            existing
        } else {
            AssetPriceEntity(
                portfolioId = portfolioId,
                symbol = request.symbol,
                currentPrice = request.currentPrice
            )
        }

        return assetPriceRepository.save(entity).toResponse()
    }

    private fun verifyPortfolioOwnership(userId: Long, portfolioId: Long) {
        if (portfolioRepository.findByIdAndUserId(portfolioId, userId) == null) {
            throw NotFoundException("Portfolio not found or access denied")
        }
    }
}
