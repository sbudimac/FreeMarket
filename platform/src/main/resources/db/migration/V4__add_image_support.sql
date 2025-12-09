

CREATE TABLE IF NOT EXISTS post_images (
    post_id UUID NOT NULL REFERENCES post(id) ON DELETE CASCADE,
    image_url TEXT NOT NULL,
    PRIMARY KEY (post_id, image_url)
);

-- Add index for faster queries
CREATE INDEX IF NOT EXISTS idx_post_images_post_id ON post_images(post_id);


INSERT INTO post_images (post_id, image_url) VALUES
-- Fresh Organic Vegetables (3 images)
((SELECT id FROM post WHERE title = 'Fresh Organic Vegetables'), 'https://images.unsplash.com/photo-1540420773420-3366772f4999?w=800&auto=format&fit=crop'),
((SELECT id FROM post WHERE title = 'Fresh Organic Vegetables'), 'https://images.unsplash.com/photo-1567306226416-28f0efdc88ce?w-800&auto=format&fit=crop'),
((SELECT id FROM post WHERE title = 'Fresh Organic Vegetables'), 'https://images.unsplash.com/photo-1579113800032-c38bd7635818?w=800&auto=format&fit=crop'),

-- Reliable Delivery Service (2 images)
((SELECT id FROM post WHERE title = 'Reliable Delivery Service'), 'https://images.unsplash.com/photo-1556742049-0cfed4f6a45d?w=800&auto=format&fit=crop'),
((SELECT id FROM post WHERE title = 'Reliable Delivery Service'), 'https://images.unsplash.com/photo-1607082348824-0a96f2a4b9da?w=800&auto=format&fit=crop'),

-- Need Construction Materials (1 image)
((SELECT id FROM post WHERE title = 'Need Construction Materials'), 'https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=800&auto=format&fit=crop'),

-- Custom Furniture Manufacturing (3 images)
((SELECT id FROM post WHERE title = 'Custom Furniture Manufacturing'), 'https://images.unsplash.com/photo-1556228453-efd6c1ff04f6?w=800&auto=format&fit=crop'),
((SELECT id FROM post WHERE title = 'Custom Furniture Manufacturing'), 'https://images.unsplash.com/photo-1507207611500-ecf4b5a6ad5a?w=800&auto=format&fit=crop'),
((SELECT id FROM post WHERE title = 'Custom Furniture Manufacturing'), 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=800&auto=format&fit=crop'),

-- Need Transport for Equipment (2 images)
((SELECT id FROM post WHERE title = 'Need Transport for Equipment'), 'https://images.unsplash.com/photo-1562907550-096b8d7c7b1a?w=800&auto=format&fit=crop'),
((SELECT id FROM post WHERE title = 'Need Transport for Equipment'), 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=800&auto=format&fit=crop')
ON CONFLICT (post_id, image_url) DO NOTHING;
