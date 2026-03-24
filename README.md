# 📈 st-viz — Stock Portfolio Visualizer

A cross-platform stock portfolio tracker where you can log buy/sell trades, manage multiple portfolios, and visualize your investment performance with sector allocation, P&L, and ROI analytics.

## ✨ Features (Phase 1)

- 👤 **Multi-user** with JWT authentication
- 📁 **Multiple portfolios** per user (e.g., "Long Term", "Trading")
- 💸 **Trade logging** — Buy/Sell with symbol, sector, quantity, price, and date
- 📊 **Analytics Dashboard** — ROI, Realized/Unrealized P&L, Sector Allocation
- 💱 **Manual price updates** — Set current market prices for held stocks
- 📱 **Cross-platform** — Android, iOS, and Web from a single Kotlin codebase

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| **Backend** | Kotlin + Spring Boot |
| **Database** | SQLite + Flyway Migrations |
| **Frontend / Mobile** | Kotlin Multiplatform (KMP) + Compose Multiplatform |
| **API** | REST / JSON with JWT Auth |
| **HTTP Client (KMP)** | Ktor |
| **Serialization** | kotlinx.serialization |

## 📁 Project Structure

```
st-viz/
├── backend/                  # Spring Boot server
│   ├── src/main/kotlin/com/stviz/
│   │   ├── user/             # Auth (register, login, JWT)
│   │   ├── portfolio/        # Portfolio CRUD
│   │   ├── transaction/      # Buy/Sell trade logging
│   │   ├── analytics/        # ROI, P&L, sector allocation
│   │   └── price/            # Manual current price entry
│   └── src/main/resources/
│       └── db/migration/     # Flyway SQL migration files
├── shared/                   # KMP shared module
│   └── src/commonMain/       # Shared models, API client, ViewModels
├── androidApp/               # Android Compose application
├── iosApp/                   # iOS application
└── .agent/                   # AI coding assistant instructions & skills
    ├── AGENT.md
    └── skills/
        ├── BACKEND_SKILL.md
        ├── KMP_FRONTEND_SKILL.md
        ├── DATABASE_SKILL.md
        └── TESTING_SKILL.md
```

## 🗄️ Database Schema

```
users ──< portfolios ──< transactions
                    └──< asset_prices
```

- **users** — Auth credentials
- **portfolios** — Named portfolios per user
- **transactions** — BUY/SELL records (symbol, sector, quantity, price, date)
- **asset_prices** — Manually entered current market price per symbol

## 🚀 Getting Started

### Prerequisites
- JDK 17+
- Android Studio (for KMP frontend)
- Gradle 8+

### Run the Backend
```bash
cd backend
./gradlew bootRun
# Server starts at http://localhost:8080
```

### Run the Android App
```bash
cd androidApp
./gradlew installDebug
```

## 📋 API Overview

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/register` | Create new user |
| `POST` | `/api/auth/login` | Login & get JWT |
| `GET` | `/api/portfolios` | List user's portfolios |
| `POST` | `/api/portfolios` | Create a portfolio |
| `GET` | `/api/portfolios/{id}/transactions` | View trade history |
| `POST` | `/api/portfolios/{id}/transactions` | Log a trade |
| `GET` | `/api/portfolios/{id}/analytics` | Get full analytics |
| `POST` | `/api/portfolios/{id}/prices` | Update current prices |

## 📊 Analytics Explained

| Metric | Formula |
|---|---|
| **Avg Buy Price** | `Total Amount Invested / Total Shares Bought` |
| **Unrealized P&L** | `(Current Price − Avg Buy Price) × Shares Held` |
| **Realized P&L** | `(Sell Price − Avg Buy Price) × Shares Sold` |
| **ROI** | `((Current Value − Total Cost) / Total Cost) × 100` |
| **Sector Allocation** | `Sector Value / Total Portfolio Value × 100` |

## 🗺️ Roadmap

- **Phase 1** ✅ Manual trade tracking, analytics, KMP app
- **Phase 2** 🔜 External stock price API integration (Alpha Vantage / Polygon.io)
- **Phase 3** 🔜 Price alerts, watchlists, push notifications

## 🤝 Contributing

See [issues.md](./issues.md) for the full list of planned features and tasks broken down into implementable units.
