package com.stviz.backend.transaction

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TransactionRepository : JpaRepository<TransactionEntity, Long> {
    fun findAllByPortfolioId(portfolioId: Long, pageable: Pageable): Page<TransactionEntity>
    fun findAllByPortfolioIdAndSymbol(portfolioId: Long, symbol: String): List<TransactionEntity>
}
