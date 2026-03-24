package com.stviz.backend.transaction

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
class TransactionControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    private var token: String = ""
    private var portfolioId: Long = 0

    @BeforeEach
    fun setUp() {
        val uniqueSuffix = java.util.UUID.randomUUID().toString().substring(0, 8)
        val username = "tx_user_$uniqueSuffix"
        val email = "tx_user_$uniqueSuffix@example.com"
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
        val createPortReq = CreatePortfolioRequest("Test Portfolio", "For Transactions")
        val portResult = mockMvc.perform(
            post("/api/portfolios")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPortReq))
        ).andReturn()
        
        portfolioId = objectMapper.readTree(portResult.response.contentAsString).get("data").get("id").asLong()
    }

    @Test
    fun `should perform BUY and SELL transactions`() {
        // 1. BUY 10 AAPL
        val buyReq = TransactionRequest(
            symbol = "AAPL",
            sector = "Technology",
            type = TransactionType.BUY,
            quantity = 10.0,
            price = 150.0,
            transactionDate = "2023-10-01T10:00:00"
        )

        mockMvc.perform(
            post("/api/portfolios/$portfolioId/transactions")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buyReq))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.data.symbol").value("AAPL"))
            .andExpect(jsonPath("$.data.quantity").value(10.0))

        // 2. SELL 5 AAPL (Happy Path)
        val sellReq = TransactionRequest(
            symbol = "AAPL",
            sector = "Technology",
            type = TransactionType.SELL,
            quantity = 5.0,
            price = 160.0,
            transactionDate = "2023-10-02T10:00:00"
        )

        mockMvc.perform(
            post("/api/portfolios/$portfolioId/transactions")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sellReq))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.data.type").value("SELL"))
            .andExpect(jsonPath("$.data.quantity").value(5.0))

        // 3. SELL 10 AAPL (Should FAIL - only 5 left)
        val sellTooMuchReq = TransactionRequest(
            symbol = "AAPL",
            sector = "Technology",
            type = TransactionType.SELL,
            quantity = 10.0,
            price = 160.0,
            transactionDate = "2023-10-03T10:00:00"
        )

        mockMvc.perform(
            post("/api/portfolios/$portfolioId/transactions")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sellTooMuchReq))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("Insufficient holdings")))
            
        // 4. GET Transactions (Paginated)
        mockMvc.perform(
            get("/api/portfolios/$portfolioId/transactions")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.content.length()").value(2))
    }

    @Test
    fun `should not allow accessing other users transactions`() {
        // Create User 2
        val uniqueSuffix = java.util.UUID.randomUUID().toString().substring(0, 8)
        val registerReq2 = RegisterRequest("tx_user2_$uniqueSuffix", "tx_user2_$uniqueSuffix@example.com", "password123")
        val registerResult2 = mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerReq2))
        ).andReturn()
        val token2 = objectMapper.readTree(registerResult2.response.contentAsString).get("data").get("token").asText()

        // User 2 tries to GET User 1's portfolio transactions
        mockMvc.perform(
            get("/api/portfolios/$portfolioId/transactions")
                .header("Authorization", "Bearer $token2")
        )
            .andExpect(status().isNotFound)
    }
}
