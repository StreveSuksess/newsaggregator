DROP TABLE IF EXISTS news_source_img_links;

ALTER TABLE news_source ADD COLUMN IF NOT EXISTS img_links JSONB DEFAULT '[]'::jsonb;

UPDATE news_source 
SET img_links = '["article"]'::jsonb 
WHERE name = 'Т-Журнал'; 