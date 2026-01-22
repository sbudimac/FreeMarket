ALTER TABLE post
ALTER COLUMN category TYPE varchar(20)
  USING category::text;