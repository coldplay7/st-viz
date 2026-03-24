# st-viz Feature Issues — Phase 1

All issues are grouped by milestone. Each issue corresponds to a discrete implementable unit of work.

---

## 🏗️ Milestone 1: Project Setup & Infrastructure

### ISSUE-001 — Initialize Spring Boot Backend Project
**Labels**: `setup`, `backend`
**Description**: Bootstrap the Spring Boot project with all required dependencies.
- [ ] Create Spring Boot project with Kotlin DSL (`build.gradle.kts`)
- [ ] Add dependencies: Spring Web, Spring Data JPA, Spring Security, SQLite JDBC, Flyway, JWT
- [ ] Configure `application.yml` for SQLite datasource
- [ ] Add `SQLiteDialect` configuration
- [ ] Confirm app starts with an empty DB

**Acceptance Criteria**: `./gradlew bootRun` starts the server on port 8080 without errors.

---

### ISSUE-002 — Initialize KMP Project with Compose Multiplatform
**Labels**: `setup`, `frontend`, `kmp`
**Description**: Bootstrap the KMP project targeting Android and iOS.
- [ ] Setup KMP Gradle project with `androidApp` and `iosApp` targets
- [ ] Add Compose Multiplatform plugin and dependencies
- [ ] Add Ktor HTTP client to `commonMain`
- [ ] Add `kotlinx.serialization` to `commonMain`
- [ ] Add Voyager navigation library

**Acceptance Criteria**: Android app runs on emulator showing a placeholder screen.

---

### ISSUE-003 — Setup Flyway DB Migrations (Schema)
**Labels**: `backend`, `database`
**Description**: Create initial Flyway SQL migrations for all Phase 1 tables.
- [ ] `V1__create_users.sql`
- [ ] `V2__create_portfolios.sql`
- [ ] `V3__create_transactions.sql`
- [ ] `V4__create_asset_prices.sql`

**Acceptance Criteria**: On app startup, all tables are created in `stviz.db`.

---

### ISSUE-004 — Global Exception Handling & API Response Envelope
**Labels**: `backend`, `infra`
**Description**: Implement a consistent API response format and global error handler.
- [ ] Create `ApiResponse<T>` data class
- [ ] Create `@ControllerAdvice` global exception handler
- [ ] Handle `NotFoundException`, `ValidationException`, `UnauthorizedException`
- [ ] Return proper HTTP status codes (400, 401, 403, 404, 500)

**Acceptance Criteria**: All errors return `{ "data": null, "error": "message", "success": false }`.

---

## 🔐 Milestone 2: User Authentication

### ISSUE-005 — User Registration API
**Labels**: `backend`, `auth`
**Description**: Implement the user registration endpoint.
- [ ] `POST /api/auth/register` — accepts `{ username, email, password }`
- [ ] Validate fields (email format, password min length 8, unique username/email)
- [ ] Hash password with BCrypt
- [ ] Store user in `users` table
- [ ] Return JWT token on success

**Acceptance Criteria**: Registering with valid data returns `201 Created` with a JWT token.

---

### ISSUE-006 — User Login API
**Labels**: `backend`, `auth`
**Description**: Implement the login endpoint.
- [ ] `POST /api/auth/login` — accepts `{ email, password }`
- [ ] Validate credentials, return `401` if invalid
- [ ] Return JWT on success

**Acceptance Criteria**: Correct credentials return `200 OK` with JWT. Wrong credentials return `401`.

---

### ISSUE-007 — JWT Security Filter
**Labels**: `backend`, `auth`
**Description**: Protect all non-auth endpoints with JWT validation.
- [ ] Implement `JwtTokenFilter` (extends `OncePerRequestFilter`)
- [ ] Configure `SecurityFilterChain` — permit `/api/auth/**`, protect all others
- [ ] Parse user from JWT and set `SecurityContext`

**Acceptance Criteria**: Protected endpoints return `401` without a valid JWT.

---

### ISSUE-008 — Login & Registration Screens (KMP)
**Labels**: `frontend`, `kmp`, `auth`
**Description**: Build the Login and Registration UI screens.
- [ ] `LoginScreen` with email/password fields and Login button
- [ ] `RegisterScreen` with username/email/password fields
- [ ] Connect to backend via Ktor API client
- [ ] Store JWT in local storage (`DataStore` / `NSUserDefaults`)
- [ ] Navigate to Portfolio list on success

**Acceptance Criteria**: User can register and log in on both Android and iOS.

---

## 📁 Milestone 3: Portfolio Management

### ISSUE-009 — Portfolio CRUD API
**Labels**: `backend`, `portfolio`
**Description**: CRUD endpoints for user portfolios.
- [ ] `GET /api/portfolios` — list portfolios for authenticated user
- [ ] `POST /api/portfolios` — create portfolio (`{ name, description }`)
- [ ] `PUT /api/portfolios/{id}` — update portfolio name/description
- [ ] `DELETE /api/portfolios/{id}` — delete portfolio (and cascade transactions)

**Acceptance Criteria**: User can only see/edit their own portfolios. Returns `403` otherwise.

---

### ISSUE-010 — Portfolio List & Create Screen (KMP)
**Labels**: `frontend`, `kmp`, `portfolio`
**Description**: Build the portfolio management UI.
- [ ] `PortfolioListScreen` — shows list of portfolios with name and quick stats
- [ ] `CreatePortfolioBottomSheet` — form to create a new portfolio
- [ ] Delete portfolio with confirmation dialog
- [ ] `PortfolioViewModel` with StateFlow states

**Acceptance Criteria**: User can view, create, and delete portfolios.

---

## 💸 Milestone 4: Transaction Logging

### ISSUE-011 — Transaction API (Buy/Sell)
**Labels**: `backend`, `transaction`
**Description**: Endpoints to log and view stock transactions.
- [ ] `GET /api/portfolios/{id}/transactions` — paginated transaction history
- [ ] `POST /api/portfolios/{id}/transactions` — log a trade
  - Body: `{ symbol, sector, type (BUY/SELL), quantity, price, date, notes? }`
- [ ] Validate: quantity > 0, price > 0, can't sell more than you hold
- [ ] `DELETE /api/portfolios/{id}/transactions/{txId}` — remove a transaction

**Acceptance Criteria**: Transactions are stored and validated correctly. Overselling is rejected.

---

### ISSUE-012 — Transaction Log Screen (KMP)
**Labels**: `frontend`, `kmp`, `transaction`
**Description**: Build the trade log UI.
- [ ] `TransactionListScreen` — paginated list of trades, grouped by date
- [ ] `AddTransactionBottomSheet` — form with symbol, sector, BUY/SELL toggle, quantity, price, date picker
- [ ] Swipe to delete transaction
- [ ] Color-coded: green for BUY, red for SELL

**Acceptance Criteria**: User can log BUY/SELL trades and view history.

---

## 📊 Milestone 5: Analytics & Dashboard

### ISSUE-013 — Manual Current Price API
**Labels**: `backend`, `analytics`
**Description**: Allow users to manually set current market prices for held stocks.
- [ ] `GET /api/portfolios/{id}/prices` — list current prices for symbols in portfolio
- [ ] `POST /api/portfolios/{id}/prices` — upsert current price for a symbol
  - Body: `{ symbol, currentPrice }`

**Acceptance Criteria**: Prices are persisted and returned correctly for a portfolio.

---

### ISSUE-014 — Analytics Calculation Engine
**Labels**: `backend`, `analytics`
**Description**: Implement all core analytics calculations in `AnalyticsService`.
- [ ] Total portfolio value (sum of `quantity_held * current_price`)
- [ ] Total invested amount
- [ ] Realized P&L (from completed sell trades)
- [ ] Unrealized P&L (from held positions)
- [ ] Overall ROI (%)
- [ ] Sector allocation (% of portfolio value per sector)
- [ ] Top gainers / losers by symbol

**Acceptance Criteria**: All calculations pass unit tests with known inputs.

---

### ISSUE-015 — Analytics API Endpoint
**Labels**: `backend`, `analytics`
**Description**: Expose analytics via a single aggregated endpoint.
- [ ] `GET /api/portfolios/{id}/analytics` — returns all metrics
  - Response: `{ totalValue, totalCost, realizedPnl, unrealizedPnl, roi, sectorAllocation[], positions[] }`

**Acceptance Criteria**: Endpoint returns correct metrics for a portfolio with known transactions.

---

### ISSUE-016 — Dashboard Screen (KMP)
**Labels**: `frontend`, `kmp`, `analytics`
**Description**: Build the main dashboard with charts and key metrics.
- [ ] Portfolio value summary card (total value, ROI badge, total P/L)
- [ ] Sector allocation **pie chart**
- [ ] Holdings list: symbol, qty, avg buy price, current price, P/L, ROI%
- [ ] Realized vs Unrealized P/L breakdown
- [ ] Color: green for positive, red for negative

**Acceptance Criteria**: Dashboard accurately reflects all calculated metrics from the backend.

---

### ISSUE-017 — Manual Price Update Screen (KMP)
**Labels**: `frontend`, `kmp`, `analytics`
**Description**: UI for the user to enter current market prices.
- [ ] `UpdatePricesScreen` — list of all held symbols with current price input fields
- [ ] "Last Updated" timestamp shown per symbol
- [ ] Save button updates all prices via API

**Acceptance Criteria**: User can update prices and dashboard reflects new values immediately.

---

## 🎨 Milestone 6: UI Polish & UX

### ISSUE-018 — App Theming & Design System (KMP)
**Labels**: `frontend`, `kmp`, `design`
**Description**: Implement a consistent design system.
- [ ] Define `AppTheme` with dark background, custom color palette
- [ ] Typography scale (Display, Title, Body, Label)
- [ ] Color tokens for gain/loss (green/red), neutral, primary
- [ ] Reusable composables: `StatCard`, `SectionHeader`, `LoadingView`, `ErrorView`

**Acceptance Criteria**: All screens use the design system with a consistent premium look.

---

### ISSUE-019 — Navigation & App Shell (KMP)
**Labels**: `frontend`, `kmp`
**Description**: Wire up all screens with bottom navigation.
- [ ] Bottom nav bar: Dashboard, Transactions, Portfolios, Settings
- [ ] Voyager navigation stack
- [ ] Handle back navigation
- [ ] Auth gate: redirect to login if no JWT

**Acceptance Criteria**: App navigates correctly between all sections.

---

## ✅ Milestone 7: Testing

### ISSUE-020 — Backend Unit Tests
**Labels**: `testing`, `backend`
**Description**: Write unit tests for all service classes.
- [ ] `UserServiceTest`
- [ ] `PortfolioServiceTest`
- [ ] `TransactionServiceTest`
- [ ] `AnalyticsServiceTest` (focus on calculation accuracy)

**Acceptance Criteria**: `./gradlew test` passes with all green.

---

### ISSUE-021 — Backend Integration Tests
**Labels**: `testing`, `backend`
**Description**: Write MockMvc integration tests for all API endpoints.
- [ ] Auth endpoints
- [ ] Portfolio CRUD
- [ ] Transaction endpoints
- [ ] Analytics endpoint

**Acceptance Criteria**: All endpoints tested for happy path and error cases.

---

## Summary Table

| Issue | Title | Milestone | Layer |
|---|---|---|---|
| ISSUE-001 | Spring Boot Setup | 1 - Setup | Backend |
| ISSUE-002 | KMP Project Setup | 1 - Setup | Frontend |
| ISSUE-003 | DB Migrations | 1 - Setup | Database |
| ISSUE-004 | Exception Handling | 1 - Setup | Backend |
| ISSUE-005 | User Registration | 2 - Auth | Backend |
| ISSUE-006 | User Login | 2 - Auth | Backend |
| ISSUE-007 | JWT Filter | 2 - Auth | Backend |
| ISSUE-008 | Login/Register UI | 2 - Auth | Frontend |
| ISSUE-009 | Portfolio CRUD API | 3 - Portfolio | Backend |
| ISSUE-010 | Portfolio UI | 3 - Portfolio | Frontend |
| ISSUE-011 | Transaction API | 4 - Transactions | Backend |
| ISSUE-012 | Transaction UI | 4 - Transactions | Frontend |
| ISSUE-013 | Manual Price API | 5 - Analytics | Backend |
| ISSUE-014 | Analytics Engine | 5 - Analytics | Backend |
| ISSUE-015 | Analytics API | 5 - Analytics | Backend |
| ISSUE-016 | Dashboard UI | 5 - Analytics | Frontend |
| ISSUE-017 | Price Update UI | 5 - Analytics | Frontend |
| ISSUE-018 | Design System | 6 - Polish | Frontend |
| ISSUE-019 | Navigation | 6 - Polish | Frontend |
| ISSUE-020 | Unit Tests | 7 - Testing | Testing |
| ISSUE-021 | Integration Tests | 7 - Testing | Testing |
