-- Enable UUID support (we use uuid_generate_v4())
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Enumerated type for post.type
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
END
$$;

-- Core tables
CREATE TABLE IF NOT EXISTS market_actor (
                                            id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username        VARCHAR(50) UNIQUE NOT NULL,
    email           VARCHAR(100) UNIQUE NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    contact_info    TEXT,
    is_verified     BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS post (
                                    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    market_actor_id UUID NOT NULL,
    type            post_type_enum NOT NULL,
    title           VARCHAR(200) NOT NULL,
    description     TEXT NOT NULL,
    location        VARCHAR(100),
    price_info      VARCHAR(100),
    contact_info    TEXT,
    created_at      TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    expires_at      TIMESTAMPTZ,
    is_active       BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_post_market_actor
    FOREIGN KEY (market_actor_id) REFERENCES market_actor(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS post_tags (
                                         post_id UUID NOT NULL,
                                         tag     VARCHAR(50) NOT NULL,
    PRIMARY KEY (post_id, tag),
    CONSTRAINT fk_post_tags_post
    FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS rating (
                                      id                      UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    rater_id                UUID NOT NULL,
    rated_market_actor_id   UUID NOT NULL,
    score                   INTEGER NOT NULL CHECK (score BETWEEN 1 AND 5),
    comment                 TEXT,
    created_at              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rating_rater
    FOREIGN KEY (rater_id) REFERENCES market_actor(id) ON DELETE CASCADE,
    CONSTRAINT fk_rating_rated
    FOREIGN KEY (rated_market_actor_id) REFERENCES market_actor(id) ON DELETE CASCADE,
    CONSTRAINT check_not_self_rating CHECK (rater_id <> rated_market_actor_id)
    );

-- Indexes
CREATE INDEX IF NOT EXISTS idx_market_actor_username     ON market_actor(username);
CREATE INDEX IF NOT EXISTS idx_market_actor_email        ON market_actor(email);
CREATE INDEX IF NOT EXISTS idx_market_actor_created_at   ON market_actor(created_at);

CREATE INDEX IF NOT EXISTS idx_post_type_active          ON post(type, is_active);
CREATE INDEX IF NOT EXISTS idx_post_location             ON post(location);
CREATE INDEX IF NOT EXISTS idx_post_created_at           ON post(created_at);
CREATE INDEX IF NOT EXISTS idx_post_market_actor_id      ON post(market_actor_id);
CREATE INDEX IF NOT EXISTS idx_post_expires_active       ON post(expires_at) WHERE is_active = TRUE;
CREATE INDEX IF NOT EXISTS idx_post_title_desc_tsv       ON post USING gin (to_tsvector('english', title || ' ' || description));

CREATE INDEX IF NOT EXISTS idx_post_tags_tag             ON post_tags(tag);
CREATE INDEX IF NOT EXISTS idx_post_tags_post_id         ON post_tags(post_id);

CREATE INDEX IF NOT EXISTS idx_rating_rated_actor        ON rating(rated_market_actor_id);
CREATE INDEX IF NOT EXISTS idx_rating_rater              ON rating(rater_id);
CREATE INDEX IF NOT EXISTS idx_rating_created_at         ON rating(created_at);
CREATE INDEX IF NOT EXISTS idx_rating_score              ON rating(score);

-- updated_at trigger
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS update_market_actor_updated_at ON market_actor;
CREATE TRIGGER update_market_actor_updated_at
    BEFORE UPDATE ON market_actor
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_post_updated_at ON post;
CREATE TRIGGER update_post_updated_at
    BEFORE UPDATE ON post
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Views
CREATE OR REPLACE VIEW active_post_view AS
SELECT
    p.id,
    p.type,
    p.title,
    p.description,
    p.location,
    p.price_info,
    p.contact_info,
    p.created_at,
    p.expires_at,
    p.is_active,
    ma.username AS market_actor_username,
    ma.is_verified AS market_actor_verified,
    ma.contact_info AS market_actor_contact,
    (SELECT COUNT(*) FROM post_tags pt WHERE pt.post_id = p.id) AS tag_count
FROM post p
         JOIN market_actor ma ON p.market_actor_id = ma.id
WHERE p.is_active = TRUE;

CREATE OR REPLACE VIEW user_rating_summary AS
SELECT
    ma.id AS market_actor_id,
    ma.username,
    COUNT(r.id) AS total_rating,
    AVG(r.score) AS average_rating,
    COUNT(CASE WHEN r.score = 5 THEN 1 END) AS five_star_rating,
    COUNT(CASE WHEN r.score = 1 THEN 1 END) AS one_star_rating
FROM market_actor ma
         LEFT JOIN rating r ON ma.id = r.rated_market_actor_id
GROUP BY ma.id, ma.username;