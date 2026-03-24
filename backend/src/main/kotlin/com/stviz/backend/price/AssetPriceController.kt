package com.stviz.backend.price

import com.stviz.backend.common.dto.ApiResponse
import com.stviz.backend.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/portfolios/{portfolioId}/prices")
class AssetPriceController(
    private val assetPriceService: AssetPriceService
) {

    @GetMapping
    fun getPrices(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable portfolioId: Long
    ): ResponseEntity<ApiResponse<List<AssetPriceResponse>>> {
        val prices = assetPriceService.getPrices(user.id, portfolioId)
        return ResponseEntity.ok(ApiResponse(data = prices))
    }

    @PostMapping
    fun updatePrice(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable portfolioId: Long,
        @Valid @RequestBody request: AssetPriceRequest
    ): ResponseEntity<ApiResponse<AssetPriceResponse>> {
        val price = assetPriceService.upsertPrice(user.id, portfolioId, request)
        return ResponseEntity.ok(ApiResponse(data = price))
    }
}
