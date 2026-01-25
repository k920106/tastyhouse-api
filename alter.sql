-- PRODUCT_BBQ 테이블에 is_options_synced 컬럼 추가
ALTER TABLE PRODUCT_BBQ
ADD COLUMN is_options_synced TINYINT(1) NOT NULL DEFAULT 0;
