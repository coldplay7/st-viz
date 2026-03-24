---
name: backend-development
description: Guidelines and patterns for developing the Spring Boot Kotlin backend for st-viz.
---

# Backend Development Skill

## Domain Package Structure
Each domain follows this exact structure:
```
{domain}/
├── {Domain}Controller.kt    # REST endpoints, input validation
├── {Domain}Service.kt       # Business logic
├── {Domain}Repository.kt    # Spring Data JPA repository interface
├── {Domain}Entity.kt        # JPA @Entity class (DB model)
└── {Domain}Dto.kt           # Request/Response DTOs
```

## Standard API Response Envelope
All endpoints return this structure:
```kotlin
data class ApiResponse<T>(
    val data: T? = null,
    val error: String? = null,
    val success: Boolean = error == null
)
```

## Creating a New Endpoint — Checklist
1. Define the JPA `@Entity` in `{Domain}Entity.kt`.
2. Create a Flyway migration SQL in `resources/db/migration/`.
3. Define `@Repository` interface extending `JpaRepository`.
4. Create request/response DTOs in `{Domain}Dto.kt`.
5. Implement business logic in `{Domain}Service.kt`.
6. Expose via `@RestController` in `{Domain}Controller.kt`.
7. Write unit tests in `src/test/kotlin/`.

## Analytics Calculation Patterns

### Realized P&L (for sold stock)
```kotlin
// (sell_price - buy_price) * quantity
val realizedPnl = sellTransaction.price.minus(buyTransaction.price).times(quantity)
```

### Unrealized P&L (for held stock)
```kotlin
// (current_price - average_buy_price) * quantity_held
val unrealizedPnl = (currentPrice - avgBuyPrice) * quantityHeld
```

### Return on Investment (ROI)
```kotlin
val roi = ((currentValue - totalCost) / totalCost) * 100
```

### Average Buy Price (FIFO / Average Cost)
For Phase 1, use **average cost basis**:
```kotlin
val avgBuyPrice = totalAmountInvested / totalQuantityBought
```

## Error Handling
Use `@ControllerAdvice` global handler. Throw domain-specific exceptions:
```kotlin
class PortfolioNotFoundException(id: Long) : RuntimeException("Portfolio $id not found")
```

## Security
- Use Spring Security with JWT filter.
- Ensure users can only access their own portfolios (check `portfolio.userId == currentUserId`).
