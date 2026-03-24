package com.stviz.backend.transaction

import com.stviz.backend.common.exception.NotFoundException
import com.stviz.backend.common.exception.ValidationException
import com.stviz.backend.portfolio.PortfolioRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val portfolioRepository: PortfolioRepository
) {

    @Transactional(readOnly = true)
    fun getTransactions(userId: Long, portfolioId: Long, pageable: Pageable): Page<TransactionResponse> {
        verifyPortfolioOwnership(userId, portfolioId)
        return transactionRepository.findAllByPortfolioId(portfolioId, pageable).map { it.toResponse() }
    }

    @Transactional
    fun createTransaction(userId: Long, portfolioId: Long, request: TransactionRequest): TransactionResponse {
        verifyPortfolioOwnership(userId, portfolioId)

        if (request.type == TransactionType.SELL) {
            val currentHolding = calculateCurrentHolding(portfolioId, request.symbol)
            if (currentHolding < request.quantity) {
                throw ValidationException("Insufficient holdings for ${request.symbol}. Current: $currentHolding, Requested SELL: ${request.quantity}")
            }
        }

        val entity = TransactionEntity(
            portfolioId = portfolioId,
            symbol = request.symbol,
            sector = request.sector,
            type = request.type,
            quantity = request.quantity,
            price = request.price,
            transactionDate = request.transactionDate,
            notes = request.notes
        )

        return transactionRepository.save(entity).toResponse()
    }

    @Transactional
    fun deleteTransaction(userId: Long, portfolioId: Long, transactionId: Long) {
        verifyPortfolioOwnership(userId, portfolioId)
        val transaction = transactionRepository.findById(transactionId).orElse(null)
            ?: throw NotFoundException("Transaction not found")
        
        if (transaction.portfolioId != portfolioId) {
            throw ValidationException("Transaction does not belong to this portfolio")
        }

        transactionRepository.delete(transaction)
    }

    private fun verifyPortfolioOwnership(userId: Long, portfolioId: Long) {
        if (portfolioRepository.findByIdAndUserId(portfolioId, userId) == null) {
            throw NotFoundException("Portfolio not found or access denied")
        }
    }

    private fun calculateCurrentHolding(portfolioId: Long, symbol: String): Double {
        val transactions = transactionRepository.findAllByPortfolioIdAndSymbol(portfolioId, symbol)
        var balance = 0.0
        for (tx in transactions) {
            if (tx.type == TransactionType.BUY) {
                balance += tx.quantity
            } else {
                balance -= tx.quantity
            }
        }
        return balance
    }
}
