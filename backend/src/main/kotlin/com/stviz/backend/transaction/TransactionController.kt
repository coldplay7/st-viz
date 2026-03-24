package com.stviz.backend.transaction

import com.stviz.backend.common.dto.ApiResponse
import com.stviz.backend.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/portfolios/{portfolioId}/transactions")
class TransactionController(
    private val transactionService: TransactionService
) {

    @GetMapping
    fun getTransactions(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable portfolioId: Long,
        pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<TransactionResponse>>> {
        val transactions = transactionService.getTransactions(user.id, portfolioId, pageable)
        return ResponseEntity.ok(ApiResponse(data = transactions))
    }

    @PostMapping
    fun createTransaction(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable portfolioId: Long,
        @Valid @RequestBody request: TransactionRequest
    ): ResponseEntity<ApiResponse<TransactionResponse>> {
        val transaction = transactionService.createTransaction(user.id, portfolioId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse(data = transaction))
    }

    @DeleteMapping("/{id}")
    fun deleteTransaction(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable portfolioId: Long,
        @PathVariable id: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        transactionService.deleteTransaction(user.id, portfolioId, id)
        return ResponseEntity.ok(ApiResponse(data = null))
    }
}
