-- FAQ_CATEGORY 테이블 생성
CREATE TABLE FAQ_CATEGORY
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    sort         INT          NOT NULL,
    is_active    TINYINT(1)   NOT NULL DEFAULT 1,
    created_at   DATETIME     NOT NULL,
    updated_at   DATETIME     NOT NULL,
    INDEX idx_faq_category_active (is_active, sort)
);

-- FAQ 테이블 생성
CREATE TABLE FAQ
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    faq_category_id BIGINT        NOT NULL,
    question        VARCHAR(500)  NOT NULL,
    answer          TEXT          NOT NULL,
    sort            INT           NOT NULL,
    is_active       TINYINT(1)    NOT NULL DEFAULT 1,
    created_at      DATETIME      NOT NULL,
    updated_at      DATETIME      NOT NULL,
    INDEX idx_faq_category_id (faq_category_id),
    INDEX idx_faq_active (faq_category_id, is_active, sort)
);
