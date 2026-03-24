package com.stviz.backend.price

import com.fasterxml.jackson.databind.ObjectMapper
import com.stviz.backend.portfolio.CreatePortfolioRequest
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
class AssetPriceControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    private var token: String = ""
    private var portfolioId: Long = 0

    @BeforeEach
    fun setUp() {
        val uniqueSuffix = java.util.UUID.randomUUID().toString().substring(0, 8)
        val username = "price_user_$uniqueSuffix"
        val email = "price_user_$uniqueSuffix@example.com"
        val password = "password123"
        
        // Register & Login to get token
        val registerReq = RegisterRequest(username, email, password)
        val mvcResult = mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerReq))
        ).andReturn()
        
        token = objectMapper.readTree(mvcResult.response.contentAsString).get("data").get("token").asText()

        // Create a portfolio
        val createPortReq = CreatePortfolioRequest("Price Test Portfolio", null)
        val portResult = mockMvc.perform(
            post("/api/portfolios")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPortReq))
        ).andReturn()
        
        portfolioId = objectMapper.readTree(portResult.response.contentAsString).get("data").get("id").asLong()
    }

    @Test
    fun `should upsert and get asset prices`() {
        // 1. Upsert Price for AAPL
        val upsertReq = AssetPriceRequest("AAPL", 155.0)
        mockMvc.perform(
            post("/api/portfolios/$portfolioId/prices")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(upsertReq))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.symbol").value("AAPL"))
            .andExpect(jsonPath("$.data.currentPrice").value(155.0))

        // 2. Update Price for AAPL
        val updateReq = AssetPriceRequest("AAPL", 160.0)
        mockMvc.perform(
            post("/api/portfolios/$portfolioId/prices")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.currentPrice").value(160.0))

        // 3. Get Prices
        mockMvc.perform(
            get("/api/portfolios/$portfolioId/prices")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data[0].symbol").value("AAPL"))
            .andExpect(jsonPath("$.data[0].currentPrice").value(160.0))
    }

    @Test
    fun `should not allow updating prices for other users portfolio`() {
        // Create User 2
        val uniqueSuffix = java.util.UUID.randomUUID().toString().substring(0, 8)
        val registerReq2 = RegisterRequest("price_user2_$uniqueSuffix", "price_user2_$uniqueSuffix@example.com", "password123")
        val registerResult2 = mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerReq2))
        ).andReturn()
        val token2 = objectMapper.readTree(registerResult2.response.contentAsString).get("data").get("token").asText()

        // User 2 tries to update User 1's portfolio price
        val upsertReq = AssetPriceRequest("AAPL", 170.0)
        mockMvc.perform(
            post("/api/portfolios/$portfolioId/prices")
                .header("Authorization", "Bearer $token2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(upsertReq))
        )
            .andExpect(status().isNotFound)
    }
}
