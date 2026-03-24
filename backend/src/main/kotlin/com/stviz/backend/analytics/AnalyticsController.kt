package com.stviz.backend.analytics

import com.stviz.backend.common.dto.ApiResponse
import com.stviz.backend.security.UserPrincipal
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/portfolios/{portfolioId}/analytics")
class AnalyticsController(
    private val analyticsService: AnalyticsService
) {

    @GetMapping
    fun getAnalytics(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable portfolioId: Long
    ): ResponseEntity<ApiResponse<PortfolioAnalyticsResponse>> {
        val analytics = analyticsService.getPortfolioAnalytics(user.id, portfolioId)
        return ResponseEntity.ok(ApiResponse(data = analytics))
    }
}
