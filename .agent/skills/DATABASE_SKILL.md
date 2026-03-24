---
name: database-migrations
description: Guidelines for managing SQLite database schema evolution using Flyway migrations.
---

# Database Migrations Skill

## Rules
1. **Never** modify an existing migration file. Always create a new one.
2. Migration files live in `backend/src/main/resources/db/migration/`.
3. Naming convention: `V{version}__{description}.sql` (e.g., `V1__create_users.sql`).
4. All table names are `snake_case` plural nouns.
5. Always define `created_at` and `updated_at` timestamps on every table.

## Migration File Convention
```sql
-- V1__create_users.sql
CREATE TABLE IF NOT EXISTS users (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    username    TEXT NOT NULL UNIQUE,
    email       TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    created_at  TEXT NOT NULL DEFAULT (datetime('now')),
    updated_at  TEXT NOT NULL DEFAULT (datetime('now'))
);
```

## Full Schema (Phase 1)

### V1 — users
| Column | Type | Notes |
|---|---|---|
| id | INTEGER PK | Auto-increment |
| username | TEXT | Unique |
| email | TEXT | Unique |
| password_hash | TEXT | BCrypt hashed |
| created_at | TEXT | ISO datetime |
| updated_at | TEXT | ISO datetime |

### V2 — portfolios
| Column | Type | Notes |
|---|---|---|
| id | INTEGER PK | Auto-increment |
| user_id | INTEGER FK | → users.id |
| name | TEXT | Portfolio name |
| description | TEXT | Optional |
| created_at | TEXT | |
| updated_at | TEXT | |

### V3 — transactions
| Column | Type | Notes |
|---|---|---|
| id | INTEGER PK | Auto-increment |
| portfolio_id | INTEGER FK | → portfolios.id |
| symbol | TEXT | e.g. AAPL |
| sector | TEXT | e.g. Technology |
| transaction_type | TEXT | BUY or SELL |
| quantity | REAL | Number of shares |
| price | REAL | Price per share |
| transaction_date | TEXT | ISO date |
| notes | TEXT | Optional |
| created_at | TEXT | |
| updated_at | TEXT | |

### V4 — asset_prices
| Column | Type | Notes |
|---|---|---|
| id | INTEGER PK | Auto-increment |
| portfolio_id | INTEGER FK | → portfolios.id |
| symbol | TEXT | e.g. AAPL |
| current_price | REAL | Manually entered |
| last_updated | TEXT | ISO datetime |

## JPA Entity Tips
- Use `@Column(name = "snake_case")` to map entity fields to DB columns.
- SQLite dialect: `org.hibernate.community.dialect.SQLiteDialect`.
- Auto-generation strategy: `GenerationType.IDENTITY`.
