CREATE TABLE IF NOT EXISTS market_actor_roles (
    market_actor_id UUID       NOT NULL REFERENCES market_actor(id) ON DELETE CASCADE,
    role           VARCHAR(50) NOT NULL,
    PRIMARY KEY (market_actor_id, role)
    );