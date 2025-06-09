DELETE FROM article_keyword;
DELETE FROM article;

ALTER SEQUENCE article_id_seq RESTART WITH 1;
ALTER SEQUENCE keyword_id_seq RESTART WITH 1; 