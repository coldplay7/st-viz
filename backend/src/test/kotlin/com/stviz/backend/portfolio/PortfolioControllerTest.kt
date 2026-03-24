package com.stviz.backend.portfolio

import com.fasterxml.jackson.databind.ObjectMapper
import com.stviz.backend.user.AuthResponse
import com.stviz.backend.user.LoginRequest
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
class PortfolioControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    private var token: String = ""
    private var userId: Long = 0

    @BeforeEach
    fun setUp() {
        val uniqueSuffix = java.util.UUID.randomUUID().toString().substring(0, 8)
        val username = "user_$uniqueSuffix"
        val email = "user_$uniqueSuffix@example.com"
        val password = "password123"
        
        // Register & Login to get token
        val registerReq = RegisterRequest(username, email, password)
        val mvcResult = mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerReq))
        ).andReturn()
        
        val responseBody = mvcResult.response.contentAsString
        val apiResponse = objectMapper.readTree(responseBody)
        token = apiResponse.get("data").get("token").asText()
    }

    @Test
    fun `should perform CRUD on portfolios`() {
        val createReq = CreatePortfolioRequest("My Savings", "Retirement fund")

        // 1. Create
        val createResult = mockMvc.perform(
            post("/api/portfolios")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReq))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.data.name").value("My Savings"))
            .andReturn()

        val portfolioId = objectMapper.readTree(createResult.response.contentAsString).get("data").get("id").asLong()

        // 2. Read (All)
        mockMvc.perform(
            get("/api/portfolios")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data[0].name").value("My Savings"))

        // 3. Update
        val updateReq = UpdatePortfolioRequest("My Big Savings", "Updated description")
        mockMvc.perform(
            put("/api/portfolios/$portfolioId")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.name").value("My Big Savings"))

        // 4. Delete
        mockMvc.perform(
            delete("/api/portfolios/$portfolioId")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)

        // 5. Verify Deleted (should be empty list)
        mockMvc.perform(
            get("/api/portfolios")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data").isEmpty)
    }

    @Test
    fun `should not allow accessing other users portfolio`() {
        // 1. User 1 creates a portfolio
        val createReq = CreatePortfolioRequest("User 1 Portfolio", null)
        val createResult = mockMvc.perform(
            post("/api/portfolios")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReq))
        ).andReturn()
        val portfolioId = objectMapper.readTree(createResult.response.contentAsString).get("data").get("id").asLong()

        // 2. Create User 2
        val uniqueSuffix = java.util.UUID.randomUUID().toString().substring(0, 8)
        val registerReq2 = RegisterRequest("user2_$uniqueSuffix", "user2_$uniqueSuffix@example.com", "password123")
        val registerResult2 = mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerReq2))
        ).andReturn()
        val token2 = objectMapper.readTree(registerResult2.response.contentAsString).get("data").get("token").asText()

        // 3. User 2 tries to update User 1's portfolio
        val updateReq = UpdatePortfolioRequest("Hacked", null)
        mockMvc.perform(
            put("/api/portfolios/$portfolioId")
                .header("Authorization", "Bearer $token2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq))
        )
            .andExpect(status().isNotFound) // Should return 404 as per service logic (findByIdAndUserId returns null)
    }
}
