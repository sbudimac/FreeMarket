-- Create tables (run automatically on first start)
CREATE TABLE IF NOT EXISTS market_actor (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    contact_info TEXT,
    is_verified BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS post (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    market_actor_id UUID NOT NULL,
    type VARCHAR(20) NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    location VARCHAR(100),
    price_info VARCHAR(100),
    contact_info TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    is_active BOOLEAN DEFAULT true
);

CREATE TABLE IF NOT EXISTS post_tags (
    post_id UUID NOT NULL,
    tag VARCHAR(255) NOT NULL,
    PRIMARY KEY (post_id, tag)
);

CREATE TABLE IF NOT EXISTS rating (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rater_id UUID NOT NULL,
    rated_market_actor_id UUID NOT NULL,
    score INTEGER NOT NULL,
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);