package com.stviz.backend.price

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AssetPriceRepository : JpaRepository<AssetPriceEntity, Long> {
    fun findAllByPortfolioId(portfolioId: Long): List<AssetPriceEntity>
    fun findByPortfolioIdAndSymbol(portfolioId: Long, symbol: String): AssetPriceEntity?
}
