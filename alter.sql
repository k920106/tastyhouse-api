# COUPON 테이블에 정률 할인 기능 추가

ALTER TABLE COUPON
    ADD COLUMN discount_type VARCHAR(20) NOT NULL DEFAULT 'AMOUNT' AFTER description;

ALTER TABLE COUPON
    ADD COLUMN max_discount_amount INT AFTER discount_amount;
