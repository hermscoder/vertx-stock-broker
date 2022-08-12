CREATE TABLE watchlist (
  account_id VARCHAR,
  asset VARCHAR,
  CONSTRAINT PK_WATCHLIST PRIMARY KEY(account_id, asset),
  CONSTRAINT FK_ASSETS FOREIGN KEY(asset)
  REFERENCES broker.assets(value)
)
