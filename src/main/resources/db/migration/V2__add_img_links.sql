CREATE TABLE news_source_img_links (
    news_source_id BIGINT NOT NULL,
    img_link_selector VARCHAR(255) NOT NULL,
    FOREIGN KEY (news_source_id) REFERENCES news_source(id) ON DELETE CASCADE
);

INSERT INTO news_source_img_links (news_source_id, img_link_selector)
SELECT id, 'article' FROM news_source WHERE name = 'Т-Журнал'; 