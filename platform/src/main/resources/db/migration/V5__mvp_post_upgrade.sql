BEGIN;

ALTER TABLE post RENAME COLUMN type TO category;

ALTER TABLE post
    ADD COLUMN view_count BIGINT NOT NULL DEFAULT 0,
    ADD COLUMN thumbnail_url TEXT,
    ADD COLUMN currency CHAR(3) NOT NULL DEFAULT 'EUR',
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    ADD COLUMN condition VARCHAR(20) DEFAULT 'USED';

UPDATE post
SET status = CASE WHEN is_active THEN 'ACTIVE' ELSE 'ARCHIVED' END;

ALTER TABLE post
    ADD CONSTRAINT post_status_check CHECK (status IN ('ACTIVE', 'SOLD', 'ARCHIVED'));

ALTER TABLE post
    ADD CONSTRAINT post_condition_check CHECK (condition IN ('NEW', 'LIKE_NEW', 'USED', 'FOR_PARTS'));

ALTER TABLE post
    ADD CONSTRAINT post_view_count_check CHECK (view_count >= 0);

ALTER TABLE post_images
    ADD COLUMN position INT NOT NULL DEFAULT 0;

UPDATE post p
SET thumbnail_url = pi.image_url
    FROM (
    SELECT post_id, MIN(image_url) AS image_url
    FROM post_images
    GROUP BY post_id
) pi
WHERE p.id = pi.post_id
  AND p.thumbnail_url IS NULL;

CREATE INDEX idx_post_status_created_at ON post(status, created_at DESC);
CREATE INDEX idx_post_category ON post(category);
CREATE INDEX idx_post_price ON post(price_info);
CREATE INDEX idx_post_tags_tag ON post_tags(tag);

COMMIT;