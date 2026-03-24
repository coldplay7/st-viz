package com.stviz.backend.portfolio

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PortfolioRepository : JpaRepository<PortfolioEntity, Long> {
    fun findAllByUserId(userId: Long): List<PortfolioEntity>
    fun findByIdAndUserId(id: Long, userId: Long): PortfolioEntity?
}
