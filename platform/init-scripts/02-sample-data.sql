-- Sample data (optional - remove if you don't want test data)
INSERT INTO market_actor (username, email, password_hash, contact_info, is_verified) VALUES
('organic_farmer', 'farmer@example.com', '$2a$10$hashed1', 'Phone: 111-2222', true),
('local_manufacturer', 'manufacturer@example.com', '$2a$10$hashed2', 'Phone: 333-4444', true),
('fast_delivery', 'delivery@example.com', '$2a$10$hashed3', 'Phone: 555-6666', true),
('tech_buyer', 'buyer@example.com', '$2a$10$hashed4', 'Phone: 777-8888', false);

INSERT INTO post (market_actor_id, type, title, description, location, price_info, contact_info, expires_at) VALUES
((SELECT id FROM market_actor WHERE username = 'organic_farmer'), 'PRODUCT_SUPPLY', 'Fresh Organic Apples', 'Fresh organic apples from local farm. Perfect quality, harvested daily.', 'Belgrade', '2.5 EUR/kg', 'Call for bulk orders', CURRENT_TIMESTAMP + INTERVAL '30 days'),
((SELECT id FROM market_actor WHERE username = 'local_manufacturer'), 'SERVICE_SUPPLY', 'CNC Machining Services', 'Precision CNC machining for metal and plastic parts. Fast turnaround.', 'Novi Sad', 'Starting from 50 EUR', 'Email for quotes', CURRENT_TIMESTAMP + INTERVAL '60 days'),
((SELECT id FROM market_actor WHERE username = 'fast_delivery'), 'DELIVERY_SUPPLY', 'Belgrade to Novi Sad Delivery', 'Daily delivery service between Belgrade and Novi Sad. Refrigerated trucks available.', 'Belgrade', '15 EUR per shipment', 'Book 24h in advance', CURRENT_TIMESTAMP + INTERVAL '90 days'),
((SELECT id FROM market_actor WHERE username = 'tech_buyer'), 'PRODUCT_DEMAND', 'Need Raspberry Pi 4 Boards', 'Looking for 50x Raspberry Pi 4 4GB boards. Urgent requirement for project.', 'Belgrade', 'Budget: 2000 EUR', 'Prefer local suppliers', CURRENT_TIMESTAMP + INTERVAL '15 days');

-- Insert tags
INSERT INTO post_tags (post_id, tag) VALUES
((SELECT id FROM post WHERE title = 'Fresh Organic Apples'), 'fruits'),
((SELECT id FROM post WHERE title = 'Fresh Organic Apples'), 'organic'),
((SELECT id FROM post WHERE title = 'Fresh Organic Apples'), 'agriculture'),
((SELECT id FROM post WHERE title = 'CNC Machining Services'), 'manufacturing'),
((SELECT id FROM post WHERE title = 'CNC Machining Services'), 'cnc'),
((SELECT id FROM post WHERE title = 'CNC Machining Services'), 'metalworking'),
((SELECT id FROM post WHERE title = 'Belgrade to Novi Sad Delivery'), 'delivery'),
((SELECT id FROM post WHERE title = 'Belgrade to Novi Sad Delivery'), 'logistics'),
((SELECT id FROM post WHERE title = 'Need Raspberry Pi 4 Boards'), 'electronics'),
((SELECT id FROM post WHERE title = 'Need Raspberry Pi 4 Boards'), 'raspberry_pi'),
((SELECT id FROM post WHERE title = 'Need Raspberry Pi 4 Boards'), 'computers');