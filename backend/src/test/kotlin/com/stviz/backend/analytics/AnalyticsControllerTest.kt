package com.stviz.backend.analytics

import com.fasterxml.jackson.databind.ObjectMapper
import com.stviz.backend.portfolio.CreatePortfolioRequest
import com.stviz.backend.price.AssetPriceRequest
import com.stviz.backend.transaction.TransactionRequest
import com.stviz.backend.transaction.TransactionType
import com.stviz.backend.user.RegisterRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
class AnalyticsControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    private var token: String = ""
    private var portfolioId: Long = 0

    @BeforeEach
    fun setUp() {
        val uniqueSuffix = java.util.UUID.randomUUID().toString().substring(0, 8)
        val username = "analytics_user_$uniqueSuffix"
        val email = "analytics_user_$uniqueSuffix@example.com"
        val password = "password123"
        
        // Register & Login
        val registerReq = RegisterRequest(username, email, password)
        val mvcResult = mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerReq))
        ).andReturn()
        
        token = objectMapper.readTree(mvcResult.response.contentAsString).get("data").get("token").asText()

        // Create Portfolio
        val createPortReq = CreatePortfolioRequest("Analytics Test", null)
        val portResult = mockMvc.perform(
            post("/api/portfolios")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPortReq))
        ).andReturn()
        
        portfolioId = objectMapper.readTree(portResult.response.contentAsString).get("data").get("id").asLong()
    }

    @Test
    fun `should calculate portfolio analytics correctly`() {
        // 1. BUY 10 AAPL at $100
        performBuy("AAPL", "Technology", 10.0, 100.0, "2023-01-01T10:00:00")
        // 2. BUY 10 AAPL at $120 (Avg: 110)
        performBuy("AAPL", "Technology", 10.0, 120.0, "2023-01-02T10:00:00")
        // 3. SELL 5 AAPL at $130 (Realized PnL: (130-110)*5 = 100)
        performSell("AAPL", "Technology", 5.0, 130.0, "2023-01-03T10:00:00")
        // 4. BUY 5 TSLA at $200
        performBuy("TSLA", "Consumer", 5.0, 200.0, "2023-01-04T10:00:00")
        
        // 5. Update Current Prices: AAPL=$140, TSLA=$180
        updatePrice("AAPL", 140.0)
        updatePrice("TSLA", 180.0)

        // 6. Verify Analytics
        // AAPL: qty=15, avg=110, cost=1650, value=2100, unrealized=450
        // TSLA: qty=5, avg=200, cost=1000, value=900, unrealized=-100
        // Total Value: 2100 + 900 = 3000
        // Total Cost: 1650 + 1000 = 2650
        // Realized PnL: 100
        // Unrealized PnL: 350
        // ROI: ((3000 + 100 - 2650) / 2650) * 100 = (450/2650)*100 = 16.98%

        mockMvc.perform(
            get("/api/portfolios/$portfolioId/analytics")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.totalValue").value(3000.0))
            .andExpect(jsonPath("$.data.totalCost").value(2650.0))
            .andExpect(jsonPath("$.data.realizedPnl").value(100.0))
            .andExpect(jsonPath("$.data.unrealizedPnl").value(350.0))
            .andExpect(jsonPath("$.data.roi").value(org.hamcrest.Matchers.closeTo(16.98, 0.01)))
            .andExpect(jsonPath("$.data.positions.length()").value(2))
    }

    private fun performBuy(symbol: String, sector: String, qty: Double, price: Double, date: String) {
        val req = TransactionRequest(symbol, sector, TransactionType.BUY, qty, price, date)
        mockMvc.perform(
            post("/api/portfolios/$portfolioId/transactions")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
        ).andExpect(status().isCreated)
    }

    private fun performSell(symbol: String, sector: String, qty: Double, price: Double, date: String) {
        val req = TransactionRequest(symbol, sector, TransactionType.SELL, qty, price, date)
        mockMvc.perform(
            post("/api/portfolios/$portfolioId/transactions")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
        ).andExpect(status().isCreated)
    }

    private fun updatePrice(symbol: String, price: Double) {
        val req = AssetPriceRequest(symbol, price)
        mockMvc.perform(
            post("/api/portfolios/$portfolioId/prices")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
        ).andExpect(status().isOk)
    }
}
