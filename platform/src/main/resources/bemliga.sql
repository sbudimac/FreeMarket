-- Create the database
CREATE DATABASE freemarket;
\c freemarket; -- Connect to the database (for PostgreSQL)

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create enum type for post types
DO $$ BEGIN
    CREATE TYPE post_type_enum AS ENUM (
        'PRODUCT_DEMAND',
        'PRODUCT_SUPPLY', 
        'SERVICE_DEMAND',
        'SERVICE_SUPPLY',
        'DELIVERY_DEMAND',
        'DELIVERY_SUPPLY'
    );
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- Create market_actor table
CREATE TABLE market_actor (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    contact_info TEXT,
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create post table
CREATE TABLE post (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    market_actor_id UUID NOT NULL,
    type post_type_enum NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    location VARCHAR(100),
    price_info VARCHAR(100),
    contact_info TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP WITH TIME ZONE,
    is_active BOOLEAN DEFAULT TRUE,
    
    -- Foreign key constraint
    CONSTRAINT fk_post_market_actor 
        FOREIGN KEY (market_actor_id) 
        REFERENCES market_actor(id) 
        ON DELETE CASCADE
);

-- Create post_tags table (for the tags element collection)
CREATE TABLE post_tags (
    post_id UUID NOT NULL,
    tag VARCHAR(50) NOT NULL,
    
    -- Primary key and foreign key constraints
    PRIMARY KEY (post_id, tag),
    CONSTRAINT fk_post_tags_post 
        FOREIGN KEY (post_id) 
        REFERENCES post(id) 
        ON DELETE CASCADE
);

-- Create rating table
CREATE TABLE rating (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    rater_id UUID NOT NULL,
    rated_market_actor_id UUID NOT NULL,
    score INTEGER NOT NULL CHECK (score >= 1 AND score <= 5),
    comment TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key constraints
    CONSTRAINT fk_rating_rater 
        FOREIGN KEY (rater_id) 
        REFERENCES market_actor(id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_rating_rated 
        FOREIGN KEY (rated_market_actor_id) 
        REFERENCES market_actor(id) 
        ON DELETE CASCADE,
    
    -- Prevent self-rating and duplicate rating
    CONSTRAINT check_not_self_rating 
        CHECK (rater_id != rated_market_actor_id)
);

-- Create indexes for better performance
CREATE INDEX idx_market_actor_username ON market_actor(username);
CREATE INDEX idx_market_actor_email ON market_actor(email);
CREATE INDEX idx_market_actor_created_at ON market_actor(created_at);

CREATE INDEX idx_post_type_active ON post(type, is_active);
CREATE INDEX idx_post_location ON post(location);
CREATE INDEX idx_post_created_at ON post(created_at);
CREATE INDEX idx_post_market_actor_id ON post(market_actor_id);
CREATE INDEX idx_post_expires_at ON post(expires_at) WHERE is_active = true;
CREATE INDEX idx_post_title_desc ON post USING gin(to_tsvector('english', title || ' ' || description));

CREATE INDEX idx_post_tags_tag ON post_tags(tag);
CREATE INDEX idx_post_tags_post_id ON post_tags(post_id);

CREATE INDEX idx_rating_rated_market_actor_id ON rating(rated_market_actor_id);
CREATE INDEX idx_rating_rater_id ON rating(rater_id);
CREATE INDEX idx_rating_created_at ON rating(created_at);
CREATE INDEX idx_rating_score ON rating(score);

-- Create unique index to prevent duplicate rating from same user
CREATE UNIQUE INDEX idx_rating_unique_rating 
ON rating(rater_id, rated_market_actor_id);

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create triggers to automatically update updated_at
CREATE TRIGGER update_market_actor_updated_at
    BEFORE UPDATE ON market_actor
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_post_updated_at
    BEFORE UPDATE ON post
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Insert sample data
INSERT INTO market_actor (username, email, password_hash, contact_info, is_verified) VALUES
('john_doe', 'john@example.com', '$2a$10$exampleHash123', 'Phone: +1234567890', true),
('jane_smith', 'jane@example.com', '$2a$10$exampleHash456', 'Email: jane@example.com', true),
('bob_wilson', 'bob@example.com', '$2a$10$exampleHash789', 'Phone: +9876543210', false),
('fresh_farms', 'info@freshfarms.com', '$2a$10$exampleHash101', 'www.freshfarms.com | Phone: +1112223333', true),
('quick_delivery', 'contact@quickdelivery.com', '$2a$10$exampleHash202', 'Phone: +4445556666', true);

INSERT INTO post (market_actor_id, type, title, description, location, price_info, contact_info, expires_at) VALUES
((SELECT id FROM market_actor WHERE username = 'fresh_farms'), 'PRODUCT_SUPPLY', 'Fresh Organic Vegetables', 'Fresh organic tomatoes, cucumbers, peppers, and lettuce from our local farm. Grown without pesticides. Available in bulk quantities.', 'Belgrade, Serbia', '5-10 EUR per kg depending on quantity', 'Call us at +1112223333 or visit our website', CURRENT_TIMESTAMP + INTERVAL '30 days'),
((SELECT id FROM market_actor WHERE username = 'quick_delivery'), 'DELIVERY_SUPPLY', 'Reliable Delivery Service', 'We provide fast and reliable delivery services in Belgrade and surrounding areas. We have refrigerated trucks for temperature-sensitive goods.', 'Belgrade, Serbia', '20-50 EUR per delivery based on distance', 'Contact: +4445556666 | Email: contact@quickdelivery.com', CURRENT_TIMESTAMP + INTERVAL '60 days'),
((SELECT id FROM market_actor WHERE username = 'bob_wilson'), 'PRODUCT_DEMAND', 'Need Construction Materials', 'Looking for construction materials for house renovation project. Need cement, bricks, sand, and wood. Large quantity required.', 'Novi Sad, Serbia', 'Budget: 5000 EUR total', 'Call Bob at +9876543210', CURRENT_TIMESTAMP + INTERVAL '14 days'),
((SELECT id FROM market_actor WHERE username = 'jane_smith'), 'SERVICE_SUPPLY', 'Custom Furniture Manufacturing', 'Professional custom furniture manufacturing service. We create tables, chairs, cabinets, and other wood furniture to your specifications.', 'Belgrade, Serbia', 'Starting from 200 EUR per piece', 'Email: jane@example.com for quotes', CURRENT_TIMESTAMP + INTERVAL '45 days'),
((SELECT id FROM market_actor WHERE username = 'john_doe'), 'DELIVERY_DEMAND', 'Need Transport for Equipment', 'Looking for reliable transport service to move industrial equipment from Belgrade to Novi Sad. Equipment is fragile and requires careful handling.', 'Belgrade to Novi Sad, Serbia', 'Budget: 300 EUR', 'Contact John: +1234567890', CURRENT_TIMESTAMP + INTERVAL '7 days');

-- Insert tags for post
INSERT INTO post_tags (post_id, tag) VALUES
((SELECT id FROM post WHERE title = 'Fresh Organic Vegetables'), 'vegetables'),
((SELECT id FROM post WHERE title = 'Fresh Organic Vegetables'), 'organic'),
((SELECT id FROM post WHERE title = 'Fresh Organic Vegetables'), 'fresh'),
((SELECT id FROM post WHERE title = 'Fresh Organic Vegetables'), 'farm'),
((SELECT id FROM post WHERE title = 'Fresh Organic Vegetables'), 'produce'),

((SELECT id FROM post WHERE title = 'Reliable Delivery Service'), 'delivery'),
((SELECT id FROM post WHERE title = 'Reliable Delivery Service'), 'transport'),
((SELECT id FROM post WHERE title = 'Reliable Delivery Service'), 'refrigerated'),
((SELECT id FROM post WHERE title = 'Reliable Delivery Service'), 'logistics'),
((SELECT id FROM post WHERE title = 'Reliable Delivery Service'), 'truck'),

((SELECT id FROM post WHERE title = 'Need Construction Materials'), 'construction'),
((SELECT id FROM post WHERE title = 'Need Construction Materials'), 'materials'),
((SELECT id FROM post WHERE title = 'Need Construction Materials'), 'renovation'),
((SELECT id FROM post WHERE title = 'Need Construction Materials'), 'cement'),
((SELECT id FROM post WHERE title = 'Need Construction Materials'), 'bricks'),

((SELECT id FROM post WHERE title = 'Custom Furniture Manufacturing'), 'furniture'),
((SELECT id FROM post WHERE title = 'Custom Furniture Manufacturing'), 'woodworking'),
((SELECT id FROM post WHERE title = 'Custom Furniture Manufacturing'), 'manufacturing'),
((SELECT id FROM post WHERE title = 'Custom Furniture Manufacturing'), 'custom'),
((SELECT id FROM post WHERE title = 'Custom Furniture Manufacturing'), 'carpentry'),

((SELECT id FROM post WHERE title = 'Need Transport for Equipment'), 'transport'),
((SELECT id FROM post WHERE title = 'Need Transport for Equipment'), 'delivery'),
((SELECT id FROM post WHERE title = 'Need Transport for Equipment'), 'equipment'),
((SELECT id FROM post WHERE title = 'Need Transport for Equipment'), 'fragile'),
((SELECT id FROM post WHERE title = 'Need Transport for Equipment'), 'industrial');

-- Insert sample rating
INSERT INTO rating (rater_id, rated_market_actor_id, score, comment, created_at) VALUES
((SELECT id FROM market_actor WHERE username = 'john_doe'), (SELECT id FROM market_actor WHERE username = 'fresh_farms'), 5, 'Excellent quality vegetables, very fresh and well packaged!', CURRENT_TIMESTAMP - INTERVAL '5 days'),
((SELECT id FROM market_actor WHERE username = 'jane_smith'), (SELECT id FROM market_actor WHERE username = 'quick_delivery'), 4, 'Reliable service, delivered on time. Good communication.', CURRENT_TIMESTAMP - INTERVAL '3 days'),
((SELECT id FROM market_actor WHERE username = 'bob_wilson'), (SELECT id FROM market_actor WHERE username = 'fresh_farms'), 5, 'Best organic produce in the region! Highly recommended.', CURRENT_TIMESTAMP - INTERVAL '10 days'),
((SELECT id FROM market_actor WHERE username = 'fresh_farms'), (SELECT id FROM market_actor WHERE username = 'quick_delivery'), 5, 'Professional delivery service, handles our fragile products with care.', CURRENT_TIMESTAMP - INTERVAL '7 days');

-- Create a view for active post with user info
CREATE VIEW active_post_view AS
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
    ma.username as market_actor_username,
    ma.is_verified as market_actor_verified,
    ma.contact_info as market_actor_contact,
    (SELECT COUNT(*) FROM post_tags pt WHERE pt.post_id = p.id) as tag_count
FROM post p
JOIN market_actor ma ON p.market_actor_id = ma.id
WHERE p.is_active = true;

-- Create a view for user rating summary
CREATE VIEW user_rating_summary AS
SELECT 
    ma.id as market_actor_id,
    ma.username,
    COUNT(r.id) as total_rating,
    AVG(r.score) as average_rating,
    COUNT(CASE WHEN r.score = 5 THEN 1 END) as five_star_rating,
    COUNT(CASE WHEN r.score = 1 THEN 1 END) as one_star_rating
FROM market_actor ma
LEFT JOIN rating r ON ma.id = r.rated_market_actor_id
GROUP BY ma.id, ma.username;

-- Grant permissions (adjust as needed for your environment)
-- GRANT ALL PRIVILEGES ON DATABASE freemarket TO your_username;
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO your_username;
-- GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO your_username;

-- Display created tables
\dt

-- Display sample data count
SELECT 
    (SELECT COUNT(*) FROM market_actor) as market_actors,
    (SELECT COUNT(*) FROM post) as post,
    (SELECT COUNT(*) FROM post_tags) as post_tags,
    (SELECT COUNT(*) FROM rating) as rating;