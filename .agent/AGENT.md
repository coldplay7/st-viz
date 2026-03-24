# Stock Visualizer — AI Coding Assistant Instructions

## Project Overview
**st-viz** is a stock portfolio tracker application. Users can log buy/sell trades, manage multiple portfolios, and visualize performance metrics (ROI, P/L, sector allocation).

## Tech Stack
| Layer | Technology |
|---|---|
| Backend | Kotlin + Spring Boot |
| Database | SQLite (via Spring Data JPA + SQLite JDBC) |
| Frontend / Mobile | Kotlin Multiplatform (KMP) + Compose Multiplatform |
| API Style | RESTful JSON |
| Auth | JWT-based token authentication |

## Monorepo Structure
```
st-viz/
├── backend/          # Spring Boot Kotlin server
│   ├── src/main/kotlin/com/stviz/
│   │   ├── user/     # User domain (auth, registration)
│   │   ├── portfolio/# Portfolio domain
│   │   ├── transaction/  # Transaction domain (buy/sell)
│   │   ├── analytics/    # Analytics calculations
│   │   └── price/    # Manual current price entries
│   └── build.gradle.kts
├── shared/           # KMP shared module (models, API client, business logic)
│   ├── commonMain/
│   ├── androidMain/
│   └── iosMain/
├── androidApp/       # Android Compose app
├── iosApp/           # iOS SwiftUI/Compose app
└── .agent/           # AI assistant instructions and skills
```

## Coding Guidelines

### Kotlin (Backend & Shared)
- Use **idiomatic Kotlin**: data classes, sealed classes, extension functions, coroutines.
- Prefer **functional style**: use `map`, `filter`, `fold` over imperative loops where appropriate.
- Use `Result<T>` or sealed error types instead of throwing exceptions at service boundaries.
- All API responses wrapped in a `ApiResponse<T>` envelope: `{ "data": ..., "error": null }`.
- Use `@ControllerAdvice` for global exception handling.
- Models must use **data classes**. Never use mutable `var` in data/model classes unless absolutely necessary.

### Spring Boot
- Follow **domain-driven structure**: each domain has its own package with `Controller`, `Service`, `Repository`, `Model`, `DTO`.
- Use **Spring Data JPA** for repository layer.
- Use **Flyway** for database migrations (SQL migration files).
- DTOs for all request/response shapes. Never expose JPA entity directly in API.
- Validate inputs using `javax.validation` annotations (`@NotBlank`, `@Positive`, etc.).

### Compose Multiplatform (KMP)
- Shared UI in `commonMain` using Compose Multiplatform.
- Use **ViewModel + StateFlow** for all UI state.
- Use **Ktor** as the HTTP client in the shared module.
- Use **kotlinx.serialization** for JSON serialization.
- Navigation via **Voyager** or **Navigation-Compose** (KMP compatible).

## Database Schema (SQLite)
See `backend/src/main/resources/db/migration/` for Flyway migration files.

Key tables: `users`, `portfolios`, `transactions`, `asset_prices`.

## Authentication
JWT tokens. `Authorization: Bearer <token>` header on all protected endpoints.

## Testing Standards
- **Unit tests**: JUnit 5 + Mockk for backend services.
- **Integration tests**: `@SpringBootTest` + MockMvc for controller layer.
- **KMP**: `kotlin.test` for shared module logic tests.

## Phase Roadmap
- **Phase 1 (Current)**: Manual data entry, core portfolio & analytics features.
- **Phase 2**: External stock price API integration (Alpha Vantage / Polygon.io).
- **Phase 3**: Notifications, price alerts, watchlists.
