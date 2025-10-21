-- Market actors
INSERT INTO market_actor (username, email, password_hash, contact_info, is_verified) VALUES
('john_doe', 'john@example.com', '$2a$10$exampleHash123', 'Phone: +1234567890', TRUE),
('jane_smith', 'jane@example.com', '$2a$10$exampleHash456', 'Email: jane@example.com', TRUE),
('bob_wilson', 'bob@example.com', '$2a$10$exampleHash789', 'Phone: +9876543210', FALSE),
('fresh_farms', 'info@freshfarms.com', '$2a$10$exampleHash101', 'www.freshfarms.com | Phone: +1112223333', TRUE),
('quick_delivery', 'contact@quickdelivery.com', '$2a$10$exampleHash202', 'Phone: +4445556666', TRUE)
ON CONFLICT (email) DO NOTHING;

-- Posts
INSERT INTO post (market_actor_id, type, title, description, location, price_info, contact_info, expires_at) VALUES
((SELECT id FROM market_actor WHERE username = 'fresh_farms'), 'PRODUCT_SUPPLY', 'Fresh Organic Vegetables', 'Fresh organic tomatoes, cucumbers, peppers, and lettuce from our local farm. Grown without pesticides. Available in bulk quantities.', 'Belgrade, Serbia', '5-10 EUR per kg depending on quantity', 'Call us at +1112223333 or visit our website', CURRENT_TIMESTAMP + INTERVAL '30 days'),
((SELECT id FROM market_actor WHERE username = 'quick_delivery'), 'DELIVERY_SUPPLY', 'Reliable Delivery Service', 'We provide fast and reliable delivery services in Belgrade and surrounding areas. We have refrigerated trucks for temperature-sensitive goods.', 'Belgrade, Serbia', '20-50 EUR per delivery based on distance', 'Contact: +4445556666 | Email: contact@quickdelivery.com', CURRENT_TIMESTAMP + INTERVAL '60 days'),
((SELECT id FROM market_actor WHERE username = 'bob_wilson'), 'PRODUCT_DEMAND', 'Need Construction Materials', 'Looking for construction materials for house renovation project. Need cement, bricks, sand, and wood. Large quantity required.', 'Novi Sad, Serbia', 'Budget: 5000 EUR total', 'Call Bob at +9876543210', CURRENT_TIMESTAMP + INTERVAL '14 days'),
((SELECT id FROM market_actor WHERE username = 'jane_smith'), 'SERVICE_SUPPLY', 'Custom Furniture Manufacturing', 'Professional custom furniture manufacturing service. We create tables, chairs, cabinets, and other wood furniture to your specifications.', 'Belgrade, Serbia', 'Starting from 200 EUR per piece', 'Email: jane@example.com for quotes', CURRENT_TIMESTAMP + INTERVAL '45 days'),
((SELECT id FROM market_actor WHERE username = 'john_doe'), 'DELIVERY_DEMAND', 'Need Transport for Equipment', 'Looking for reliable transport service to move industrial equipment from Belgrade to Novi Sad. Equipment is fragile and requires careful handling.', 'Belgrade to Novi Sad, Serbia', 'Budget: 300 EUR', 'Contact John: +1234567890', CURRENT_TIMESTAMP + INTERVAL '7 days');

-- Tags
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

-- Ratings
INSERT INTO rating (rater_id, rated_market_actor_id, score, comment, created_at) VALUES
((SELECT id FROM market_actor WHERE username = 'john_doe'), (SELECT id FROM market_actor WHERE username = 'fresh_farms'), 5, 'Excellent quality vegetables, very fresh and well packaged!', CURRENT_TIMESTAMP - INTERVAL '5 days'),
((SELECT id FROM market_actor WHERE username = 'jane_smith'), (SELECT id FROM market_actor WHERE username = 'quick_delivery'), 4, 'Reliable service, delivered on time. Good communication.', CURRENT_TIMESTAMP - INTERVAL '3 days'),
((SELECT id FROM market_actor WHERE username = 'bob_wilson'), (SELECT id FROM market_actor WHERE username = 'fresh_farms'), 5, 'Best organic produce in the region! Highly recommended.', CURRENT_TIMESTAMP - INTERVAL '10 days'),
((SELECT id FROM market_actor WHERE username = 'fresh_farms'), (SELECT id FROM market_actor WHERE username = 'quick_delivery'), 5, 'Professional delivery service, handles our fragile products with care.', CURRENT_TIMESTAMP - INTERVAL '7 days');
