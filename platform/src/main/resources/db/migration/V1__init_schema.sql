-- Enable required extensions
CREATE EXTENSION IF NOT EXISTS pgcrypto;  -- for gen_random_uuid()
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";  -- optional, but common for legacy UUID tools

-- Create enum for post types
DO $$
BEGIN
    CREATE TYPE post_type_enum AS ENUM (
        'PRODUCT_DEMAND',
        'PRODUCT_SUPPLY',
        'SERVICE_DEMAND',
        'SERVICE_SUPPLY',
        'DELIVERY_DEMAND',
        'DELIVERY_SUPPLY'
    );
EXCEPTION
    WHEN duplicate_object THEN NULL;
END $$;

-- Market actors (users)
CREATE TABLE IF NOT EXISTS market_actor (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username      VARCHAR(50)  NOT NULL UNIQUE,
    email         VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    contact_info  TEXT,
    is_verified   BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Posts
CREATE TABLE IF NOT EXISTS post (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    market_actor_id UUID          NOT NULL REFERENCES market_actor(id) ON DELETE CASCADE,
    type            post_type_enum NOT NULL,
    title           VARCHAR(200)  NOT NULL,
    description     TEXT          NOT NULL,
    location        VARCHAR(100),
    price_info      VARCHAR(100),
    contact_info    TEXT,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    expires_at      TIMESTAMPTZ,
    is_active       BOOLEAN       NOT NULL DEFAULT TRUE
);

-- Tags for posts (many-to-many relationship)
CREATE TABLE IF NOT EXISTS post_tags (
    post_id UUID NOT NULL REFERENCES post(id) ON DELETE CASCADE,
    tag     VARCHAR(50) NOT NULL,
    PRIMARY KEY (post_id, tag)
);

-- Ratings
CREATE TABLE IF NOT EXISTS rating (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rater_id              UUID NOT NULL REFERENCES market_actor(id) ON DELETE CASCADE,
    rated_market_actor_id UUID NOT NULL REFERENCES market_actor(id) ON DELETE CASCADE,
    score                 INTEGER NOT NULL CHECK (score BETWEEN 1 AND 5),
    comment               TEXT,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT check_not_self_rating CHECK (rater_id <> rated_market_actor_id)
);

-- Minimal indexes (foreign keys are not auto-indexed)
CREATE INDEX IF NOT EXISTS idx_post_market_actor_id         ON post(market_actor_id);
CREATE INDEX IF NOT EXISTS idx_rating_rated_market_actor_id ON rating(rated_market_actor_id);
CREATE INDEX IF NOT EXISTS idx_rating_rater_id              ON rating(rater_id);
