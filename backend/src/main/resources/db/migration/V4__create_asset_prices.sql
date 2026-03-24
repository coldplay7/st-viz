CREATE TABLE IF NOT EXISTS asset_prices (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    portfolio_id   INTEGER NOT NULL,
    symbol         TEXT NOT NULL,
    current_price  REAL NOT NULL,
    last_updated   TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY(portfolio_id) REFERENCES portfolios(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX idx_portfolio_symbol ON asset_prices (portfolio_id, symbol);
