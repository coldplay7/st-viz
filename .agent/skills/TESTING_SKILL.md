---
name: testing
description: Testing conventions and patterns for the st-viz backend and shared KMP module.
---

# Testing Skill

## Backend Tests (JUnit 5 + Mockk)

### Unit Test — Service Layer
```kotlin
@ExtendWith(MockKExtension::class)
class PortfolioServiceTest {
    @MockK lateinit var portfolioRepository: PortfolioRepository
    @InjectMockKs lateinit var portfolioService: PortfolioService

    @Test
    fun `should return portfolios for user`() {
        every { portfolioRepository.findByUserId(1L) } returns listOf(
            PortfolioEntity(id = 1L, userId = 1L, name = "Test", description = "")
        )
        val result = portfolioService.getPortfoliosForUser(1L)
        assertThat(result).hasSize(1)
        verify { portfolioRepository.findByUserId(1L) }
    }
}
```

### Integration Test — Controller Layer
```kotlin
@SpringBootTest
@AutoConfigureMockMvc
class PortfolioControllerTest {
    @Autowired lateinit var mockMvc: MockMvc

    @Test
    fun `GET portfolios returns 200`() {
        mockMvc.get("/api/portfolios") {
            header("Authorization", "Bearer $testToken")
        }.andExpect {
            status { isOk() }
            jsonPath("$.data") { isArray() }
        }
    }
}
```

## Analytics Unit Tests
Always test the calculation logic independently in `AnalyticsServiceTest`:
- ROI calculation with known inputs
- Unrealized P/L with manual current price
- Sector allocation percentages sum to 100%
- Edge case: no transactions in portfolio

## KMP Shared Tests
Use `kotlin.test` in `commonTest`:
```kotlin
class PortfolioViewModelTest {
    @Test
    fun `initial state is loading`() = runTest {
        val vm = PortfolioViewModel(FakePortfolioRepository())
        assertEquals(PortfolioState.Loading, vm.state.value)
    }
}
```

## What To Test Per Issue
| Feature | Unit Test | Integration Test |
|---|---|---|
| User Registration | Password hashing, validation | POST /register returns 201 |
| Portfolio CRUD | Service logic | CRUD endpoints |
| Transaction Logging | BUY/SELL balance | POST /transactions |
| Analytics | ROI, P/L formulas | GET /analytics |
| Manual Price Update | Price stored correctly | POST /prices |
