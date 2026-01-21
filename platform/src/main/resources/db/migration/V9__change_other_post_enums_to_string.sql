ALTER TABLE post
ALTER COLUMN status TYPE varchar(20)
  USING status::text;

ALTER TABLE post
ALTER COLUMN condition TYPE varchar(20)
  USING condition::text;
