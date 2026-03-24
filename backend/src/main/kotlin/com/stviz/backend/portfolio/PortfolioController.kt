package com.stviz.backend.portfolio

import com.stviz.backend.common.dto.ApiResponse
import com.stviz.backend.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/portfolios")
class PortfolioController(
    private val portfolioService: PortfolioService
) {

    @GetMapping
    fun getAllPortfolios(
        @AuthenticationPrincipal user: UserPrincipal
    ): ResponseEntity<ApiResponse<List<PortfolioResponse>>> {
        val portfolios = portfolioService.getUserPortfolios(user.id)
        return ResponseEntity.ok(ApiResponse(data = portfolios))
    }

    @PostMapping
    fun createPortfolio(
        @AuthenticationPrincipal user: UserPrincipal,
        @Valid @RequestBody request: CreatePortfolioRequest
    ): ResponseEntity<ApiResponse<PortfolioResponse>> {
        val portfolio = portfolioService.createPortfolio(user.id, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse(data = portfolio))
    }

    @PutMapping("/{id}")
    fun updatePortfolio(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdatePortfolioRequest
    ): ResponseEntity<ApiResponse<PortfolioResponse>> {
        val portfolio = portfolioService.updatePortfolio(user.id, id, request)
        return ResponseEntity.ok(ApiResponse(data = portfolio))
    }

    @DeleteMapping("/{id}")
    fun deletePortfolio(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable id: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        portfolioService.deletePortfolio(user.id, id)
        return ResponseEntity.ok(ApiResponse(data = null))
    }
}
