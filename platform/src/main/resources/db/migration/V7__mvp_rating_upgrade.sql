BEGIN;

ALTER TABLE rating
    ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();

ALTER TABLE rating
    RENAME COLUMN rated_market_actor_id TO ratee_id;

ALTER TABLE rating
DROP CONSTRAINT check_not_self_rating;

ALTER TABLE rating
    ADD CONSTRAINT check_not_self_rating CHECK (rater_id <> ratee_id);

ALTER TABLE rating
    ADD CONSTRAINT uq_rating_rater_ratee UNIQUE (rater_id, ratee_id);

DROP INDEX idx_rating_rated_market_actor_id;

CREATE INDEX idx_rating_ratee_id ON rating(ratee_id);

COMMIT;
