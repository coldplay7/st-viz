package com.stviz.backend.portfolio

import com.stviz.backend.common.exception.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class PortfolioService(
    private val portfolioRepository: PortfolioRepository
) {
    fun getUserPortfolios(userId: Long): List<PortfolioResponse> {
        return portfolioRepository.findAllByUserId(userId).map { it.toResponse() }
    }

    @Transactional
    fun createPortfolio(userId: Long, request: CreatePortfolioRequest): PortfolioResponse {
        val entity = PortfolioEntity(
            userId = userId,
            name = request.name,
            description = request.description
        )
        return portfolioRepository.save(entity).toResponse()
    }

    @Transactional
    fun updatePortfolio(userId: Long, portfolioId: Long, request: UpdatePortfolioRequest): PortfolioResponse {
        val entity = portfolioRepository.findByIdAndUserId(portfolioId, userId)
            ?: throw NotFoundException("Portfolio not found or access denied")
            
        entity.name = request.name
        entity.description = request.description
        entity.updatedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        
        return portfolioRepository.save(entity).toResponse()
    }

    @Transactional
    fun deletePortfolio(userId: Long, portfolioId: Long) {
        val entity = portfolioRepository.findByIdAndUserId(portfolioId, userId)
            ?: throw NotFoundException("Portfolio not found or access denied")
            
        portfolioRepository.delete(entity)
    }
}
