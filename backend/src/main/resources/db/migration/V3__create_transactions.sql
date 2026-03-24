CREATE TABLE IF NOT EXISTS transactions (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    portfolio_id     INTEGER NOT NULL,
    symbol           TEXT NOT NULL,
    sector           TEXT NOT NULL,
    transaction_type TEXT NOT NULL,
    quantity         REAL NOT NULL,
    price            REAL NOT NULL,
    transaction_date TEXT NOT NULL,
    notes            TEXT,
    created_at       TEXT NOT NULL DEFAULT (datetime('now')),
    updated_at       TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY(portfolio_id) REFERENCES portfolios(id) ON DELETE CASCADE
);
