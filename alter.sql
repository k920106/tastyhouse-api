# COUPON 테이블에 정률 할인 기능 추가

ALTER TABLE COUPON
    ADD COLUMN discount_type VARCHAR(20) NOT NULL DEFAULT 'AMOUNT' AFTER description;

ALTER TABLE COUPON
    ADD COLUMN max_discount_amount INT AFTER discount_amount;

# EVENT 테이블 이미지 URL → 파일 ID 변경
ALTER TABLE EVENT
    ADD COLUMN thumbnail_image_file_id BIGINT AFTER subtitle,
    ADD COLUMN banner_image_file_id BIGINT AFTER thumbnail_image_file_id;

ALTER TABLE EVENT
    DROP COLUMN thumbnail_image_url,
    DROP COLUMN banner_image_url;

# EVENT_PRIZE 테이블 이미지 URL → 파일 ID 변경
ALTER TABLE EVENT_PRIZE
    ADD COLUMN image_file_id BIGINT AFTER brand;

ALTER TABLE EVENT_PRIZE
    DROP COLUMN image_url;
