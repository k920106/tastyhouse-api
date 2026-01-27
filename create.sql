# ABCDEFGHIJKLMNOPQRSTUVWXYZ

CREATE TABLE BANNER
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    title      VARCHAR(100),
    image_url  VARCHAR(255) NOT NULL,
    link_url   VARCHAR(500),
    start_date DATETIME,
    end_date   DATETIME,
    sort       INT          NOT NULL,
    is_active  TINYINT(1)   NOT NULL DEFAULT 1,
    created_at DATETIME     NOT NULL,
    updated_at DATETIME     NOT NULL
);

CREATE TABLE BUG_REPORT
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id  BIGINT       NOT NULL,
    device     VARCHAR(100) NOT NULL,
    title      VARCHAR(200) NOT NULL,
    content    TEXT         NOT NULL,
    created_at DATETIME     NOT NULL,
    updated_at DATETIME     NOT NULL,
    INDEX idx_bug_report_member_id (member_id)
);

CREATE TABLE BUG_REPORT_IMAGE
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    bug_report_id BIGINT       NOT NULL,
    image_url     VARCHAR(255) NOT NULL,
    sort          INT          NOT NULL,
    created_at    DATETIME     NOT NULL,
    updated_at    DATETIME     NOT NULL,
    INDEX idx_bug_report_image_bug_report_id (bug_report_id)
);

CREATE TABLE COUPON
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    name               VARCHAR(200) NOT NULL,
    description        VARCHAR(500),
    discount_amount    INT          NOT NULL,
    min_order_amount   INT          NOT NULL DEFAULT 0,
    max_discount_count INT,
    issue_start_at     DATETIME     NOT NULL,
    issue_end_at       DATETIME     NOT NULL,
    use_start_at       DATETIME     NOT NULL,
    use_end_at         DATETIME     NOT NULL,
    is_active          TINYINT(1)   NOT NULL DEFAULT 1,
    created_at         DATETIME     NOT NULL,
    updated_at         DATETIME     NOT NULL,
    INDEX idx_coupon_active (is_active),
    INDEX idx_coupon_issue_period (issue_start_at, issue_end_at),
    INDEX idx_coupon_use_period (use_start_at, use_end_at)
);

CREATE TABLE MEMBER_COUPON
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id  BIGINT      NOT NULL,
    coupon_id  BIGINT      NOT NULL,
    is_used    TINYINT(1)  NOT NULL DEFAULT 0,
    used_at    DATETIME,
    expired_at DATETIME    NOT NULL,
    created_at DATETIME    NOT NULL,
    updated_at DATETIME    NOT NULL,
    UNIQUE KEY uk_member_coupon (member_id, coupon_id),
    INDEX idx_member_coupon_member_id (member_id),
    INDEX idx_member_coupon_coupon_id (coupon_id),
    INDEX idx_member_coupon_used (member_id, is_used)
);

CREATE TABLE EVENT_PRIZE
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id   BIGINT       NOT NULL,
    prize_rank INT          NOT NULL,
    name       VARCHAR(200) NOT NULL,
    brand      VARCHAR(100) NOT NULL,
    image_url  VARCHAR(500),
    created_at DATETIME     NOT NULL,
    updated_at DATETIME     NOT NULL,
    UNIQUE KEY uk_event_prize_rank (event_id, prize_rank),
    INDEX idx_event_prize (event_id, prize_rank),
    INDEX idx_prize_brand (brand),
    INDEX idx_prize_name (name)
);

CREATE TABLE EVENT_WINNER
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id     BIGINT      NOT NULL,
    rank_no      INT         NOT NULL,
    winner_name  VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    announced_at DATETIME    NOT NULL,
    created_at   DATETIME    NOT NULL,
    updated_at   DATETIME    NOT NULL,
    INDEX idx_event_winner_event_id (event_id),
    INDEX idx_event_winner_announced_at (announced_at)
);

CREATE TABLE EVENT
(
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                VARCHAR(200) NOT NULL,
    description         VARCHAR(1000),
    subtitle            VARCHAR(200),
    thumbnail_image_url VARCHAR(500),
    banner_image_url    VARCHAR(500),
    content_html        TEXT,
    type                VARCHAR(50)  NOT NULL DEFAULT 'RANKING',
    status              VARCHAR(20)  NOT NULL,
    start_at            DATETIME     NOT NULL,
    end_at              DATETIME     NOT NULL,
    created_at          DATETIME     NOT NULL,
    updated_at          DATETIME     NOT NULL,
    INDEX idx_event_status (status),
    INDEX idx_event_period (start_at, end_at)
);

CREATE TABLE FOLLOW
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    follower_id  BIGINT   NOT NULL,
    following_id BIGINT   NOT NULL,
    created_at   DATETIME NOT NULL,
    updated_at   DATETIME NOT NULL,
    UNIQUE KEY uk_follow_follower_following (follower_id, following_id),
    INDEX idx_follow_follower_id (follower_id),
    INDEX idx_follow_following_id (following_id)
);

CREATE TABLE MEMBER
(
    id                        BIGINT AUTO_INCREMENT PRIMARY KEY,
    username                  VARCHAR(50)  NOT NULL UNIQUE,
    password                  VARCHAR(255) NOT NULL,
    nickname                  VARCHAR(50)  NOT NULL,
    full_name                 VARCHAR(100) NOT NULL,
    birth_date                INT,
    gender                    VARCHAR(10)  NOT NULL,
    phone_number              VARCHAR(20),
    member_grade              VARCHAR(20)  NOT NULL DEFAULT 'NEWCOMER',
    profile_image_url         VARCHAR(500),
    status_message            VARCHAR(200),
    push_notification_enabled TINYINT(1)   NOT NULL DEFAULT 1,
    marketing_info_enabled    TINYINT(1)   NOT NULL DEFAULT 0,
    event_info_enabled        TINYINT(1)   NOT NULL DEFAULT 0,
    member_status             VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at                DATETIME     NOT NULL,
    updated_at                DATETIME     NOT NULL
);

CREATE TABLE MEMBER_REVIEW_RANK
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id      BIGINT      NOT NULL,
    review_count   INT         NOT NULL,
    rank_no        INT         NOT NULL,
    rank_type      VARCHAR(20) NOT NULL,
    base_date      DATE        NOT NULL,
    last_review_at DATETIME,
    created_at     DATETIME    NOT NULL,
    updated_at     DATETIME    NOT NULL,
    UNIQUE KEY uk_member_rank (member_id, rank_type, base_date),
    INDEX idx_rank_query (rank_type, base_date, rank_no),
    INDEX idx_member_rank (member_id, rank_type)
);

CREATE TABLE PRODUCT
(
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    place_id            BIGINT        NOT NULL,
    product_category_id BIGINT,
    name                VARCHAR(255)  NOT NULL,
    description         VARCHAR(1000),
    price               INT           NOT NULL,
    discount_price      INT,
    discount_rate       DECIMAL(19, 2),
    rating              DOUBLE,
    review_count        INT           DEFAULT 0,
    is_representative   TINYINT(1)    DEFAULT 0,
    spiciness           INT,
    is_sold_out         TINYINT(1)    NOT NULL DEFAULT 0,
    is_active           TINYINT(1)    NOT NULL DEFAULT 1,
    sort                INT           NOT NULL,
    created_at          DATETIME      NOT NULL,
    updated_at          DATETIME      NOT NULL,
    INDEX idx_product_place_id (place_id),
    INDEX idx_product_category (place_id, product_category_id),
    INDEX idx_product_representative (place_id, is_representative),
    INDEX idx_product_active (place_id, is_active, sort)
);

CREATE TABLE PRODUCT_BBQ
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id      BIGINT   NOT NULL UNIQUE,
    bbq_menu_id     BIGINT   NOT NULL,
    bbq_category_id BIGINT,
    is_options_synced TINYINT(1) NOT NULL DEFAULT 0,
    created_at      DATETIME NOT NULL,
    updated_at      DATETIME NOT NULL,
    INDEX idx_product_bbq_product_id (product_id),
    INDEX idx_product_bbq_menu_id (bbq_menu_id),
    INDEX idx_product_bbq_category_id (bbq_category_id)
);

CREATE TABLE PRODUCT_IMAGE
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT       NOT NULL,
    image_url  VARCHAR(255) NOT NULL,
    sort       INT          NOT NULL,
    is_active  TINYINT(1)   NOT NULL DEFAULT 1,
    created_at DATETIME     NOT NULL,
    updated_at DATETIME     NOT NULL,
    INDEX idx_product_image_product_id (product_id),
    INDEX idx_product_image_active (product_id, is_active, sort)
);

CREATE TABLE PRODUCT_CATEGORY
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    place_id      BIGINT       NOT NULL,
    name  VARCHAR(100) NOT NULL,
    sort          INT          NOT NULL,
    is_active     TINYINT(1)   NOT NULL DEFAULT 1,
    created_at    DATETIME     NOT NULL,
    updated_at    DATETIME     NOT NULL,
    INDEX idx_product_category_place_id (place_id),
    INDEX idx_product_category_active (place_id, is_active, sort)
);

CREATE TABLE PRODUCT_COMMON_OPTION_GROUP
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id         BIGINT       NOT NULL,
    name               VARCHAR(100) NOT NULL,
    description        VARCHAR(500),
    is_required        TINYINT(1)   NOT NULL DEFAULT 0,
    is_multiple_select TINYINT(1)   NOT NULL DEFAULT 0,
    min_select         INT,
    max_select         INT,
    sort               INT          NOT NULL,
    is_active          TINYINT(1)   NOT NULL DEFAULT 1,
    created_at         DATETIME     NOT NULL,
    updated_at         DATETIME     NOT NULL,
    INDEX idx_product_common_option_group_product_id (product_id),
    INDEX idx_product_common_option_group_active (product_id, is_active, sort)
);

CREATE TABLE PRODUCT_COMMON_OPTION
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    option_group_id  BIGINT       NOT NULL,
    name             VARCHAR(100) NOT NULL,
    additional_price INT          NOT NULL DEFAULT 0,
    sort             INT          NOT NULL,
    is_sold_out      TINYINT(1)   NOT NULL DEFAULT 0,
    is_active        TINYINT(1)   NOT NULL DEFAULT 1,
    created_at       DATETIME     NOT NULL,
    updated_at       DATETIME     NOT NULL,
    INDEX idx_product_common_option_group_id (option_group_id),
    INDEX idx_product_common_option_active (option_group_id, is_active, sort)
);

CREATE TABLE PRODUCT_OPTION
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    option_group_id  BIGINT       NOT NULL,
    name             VARCHAR(100) NOT NULL,
    additional_price INT          NOT NULL DEFAULT 0,
    sort             INT          NOT NULL,
    is_sold_out      TINYINT(1)   NOT NULL DEFAULT 0,
    is_active        TINYINT(1)   NOT NULL DEFAULT 1,
    created_at       DATETIME     NOT NULL,
    updated_at       DATETIME     NOT NULL,
    INDEX idx_product_option_group_id (option_group_id),
    INDEX idx_product_option_active (option_group_id, is_active, sort)
);

CREATE TABLE PRODUCT_OPTION_GROUP
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id         BIGINT       NOT NULL,
    name               VARCHAR(100) NOT NULL,
    description        VARCHAR(500),
    is_required        TINYINT(1)   NOT NULL DEFAULT 0,
    is_multiple_select TINYINT(1)   NOT NULL DEFAULT 0,
    min_select         INT,
    max_select         INT,
    sort               INT          NOT NULL,
    is_active          TINYINT(1)   NOT NULL DEFAULT 1,
    created_at         DATETIME     NOT NULL,
    updated_at         DATETIME     NOT NULL,
    INDEX idx_product_option_group_product_id (product_id),
    INDEX idx_product_option_group_active (product_id, is_active, sort)
);

CREATE TABLE PLACE
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    station_id        BIGINT        NOT NULL,
    name              VARCHAR(255)  NOT NULL UNIQUE,
    latitude          DECIMAL(9, 6) NOT NULL,
    longitude         DECIMAL(9, 6) NOT NULL,
    rating            DOUBLE,
    road_address      VARCHAR(500),
    lot_address       VARCHAR(500),
    phone_number      VARCHAR(20),
    closed_days       VARCHAR(100),
    thumbnailImageUrl VARCHAR(255),
    created_at        DATETIME      NOT NULL,
    updated_at        DATETIME      NOT NULL
);

CREATE TABLE MEMBER_POINT
(
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id               BIGINT   NOT NULL UNIQUE,
    available_points        INT      NOT NULL DEFAULT 0,
    expired_this_month      INT      NOT NULL DEFAULT 0,
    created_at              DATETIME NOT NULL,
    updated_at              DATETIME NOT NULL,
    INDEX idx_member_point_member_id (member_id)
);

CREATE TABLE MEMBER_POINT_HISTORY
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id         BIGINT       NOT NULL,
    point_type        VARCHAR(50)  NOT NULL,
    point_amount      INT          NOT NULL,
    reason            VARCHAR(200) NOT NULL,
    created_at        DATETIME     NOT NULL,
    updated_at        DATETIME     NOT NULL,
    INDEX idx_member_point_history_member_id (member_id),
    INDEX idx_member_point_history_created_at (created_at)
);

CREATE TABLE NOTICE
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    title      VARCHAR(200) NOT NULL,
    content    TEXT         NOT NULL,
    is_active  TINYINT(1)   NOT NULL DEFAULT 1,
    created_at DATETIME     NOT NULL,
    updated_at DATETIME     NOT NULL,
    INDEX idx_notice_active (is_active),
    INDEX idx_notice_created_at (created_at)
);

CREATE TABLE PARTNERSHIP_REQUEST
(
    id                         BIGINT AUTO_INCREMENT PRIMARY KEY,
    business_name              VARCHAR(200) NOT NULL,
    address                    VARCHAR(500) NOT NULL,
    address_detail             VARCHAR(500),
    contact_name               VARCHAR(100) NOT NULL,
    contact_phone              VARCHAR(20)  NOT NULL,
    consultation_requested_at  DATETIME     NOT NULL,
    created_at                 DATETIME     NOT NULL,
    updated_at                 DATETIME     NOT NULL,
    INDEX idx_partnership_request_business_name (business_name),
    INDEX idx_partnership_request_consultation_date (consultation_requested_at)
);

CREATE TABLE PLACE_REPORT
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    business_name  VARCHAR(200) NOT NULL,
    address        VARCHAR(500) NOT NULL,
    address_detail VARCHAR(500),
    created_at     DATETIME     NOT NULL,
    updated_at     DATETIME     NOT NULL,
    INDEX idx_place_report_business_name (business_name)
);

CREATE TABLE PLACE_OWNER_MESSAGE_HISTORY
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    place_id      BIGINT   NOT NULL,
    message       TEXT,
    created_at    DATETIME NOT NULL,
    updated_at    DATETIME NOT NULL,
    INDEX idx_place_owner_message_history_place_id (place_id)
);

CREATE TABLE PLACE_AMENITY_CATEGORY
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    amenity       VARCHAR(50)  NOT NULL UNIQUE,
    display_name  VARCHAR(100) NOT NULL,
    image_url_on  VARCHAR(255) NOT NULL,
    image_url_off VARCHAR(255) NOT NULL,
    sort          INT          NOT NULL,
    is_active     TINYINT(1)   NOT NULL DEFAULT 1,
    created_at    DATETIME     NOT NULL,
    updated_at    DATETIME     NOT NULL,
    INDEX idx_amenity_category_active (is_active, sort)
);

CREATE TABLE PLACE_AMENITY
(
    id                        BIGINT AUTO_INCREMENT PRIMARY KEY,
    place_id                  BIGINT NOT NULL,
    place_amenity_category_id BIGINT NOT NULL,
    UNIQUE KEY uk_place_amenity (place_id, place_amenity_category_id),
    INDEX idx_place_amenity_place_id (place_id)
);

CREATE TABLE PLACE_BOOKMARK
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    place_id   BIGINT   NOT NULL,
    member_id  BIGINT   NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    UNIQUE KEY uk_place_bookmark (place_id, member_id),
    INDEX idx_place_bookmark_place_id (place_id),
    INDEX idx_place_bookmark_member_id (member_id)
);

CREATE TABLE PLACE_BUSINESS_HOUR
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    place_id         BIGINT      NOT NULL,
    day_type         VARCHAR(20) NOT NULL,
    open_time        TIME,
    close_time       TIME,
    is_closed        TINYINT(1),
    INDEX idx_place_business_hour_place_id (place_id),
    UNIQUE KEY uk_place_business_hour (place_id, day_type)
);

CREATE TABLE PLACE_BREAK_TIME
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    place_id         BIGINT      NOT NULL,
    day_type         VARCHAR(20) NOT NULL,
    start_time TIME        NOT NULL,
    end_time   TIME        NOT NULL,
    INDEX idx_place_break_time_place_id (place_id),
    UNIQUE KEY uk_place_break_time (place_id, day_type)
);

CREATE TABLE PLACE_CLOSED_DAY
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    place_id         BIGINT      NOT NULL,
    closed_day_type  VARCHAR(50) NOT NULL,
    INDEX idx_place_closed_day_place_id (place_id)
);

CREATE TABLE PLACE_ORDER_METHOD
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    place_id     BIGINT       NOT NULL,
    order_method VARCHAR(50)  NOT NULL,
    created_at   DATETIME     NOT NULL,
    updated_at   DATETIME     NOT NULL,
    UNIQUE KEY uk_place_order_method (place_id, order_method),
    INDEX idx_place_order_method_place_id (place_id)
);

CREATE TABLE PLACE_CHOICE
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    place_id   BIGINT       NOT NULL,
    title      VARCHAR(200) NOT NULL,
    content    TEXT,
    created_at DATETIME     NOT NULL,
    updated_at DATETIME     NOT NULL
);

CREATE TABLE PLACE_FOOD_TYPE_CATEGORY
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    food_type     VARCHAR(50)  NOT NULL UNIQUE,
    display_name  VARCHAR(100) NOT NULL,
    image_url_on  VARCHAR(255) NOT NULL,
    image_url_off VARCHAR(255) NOT NULL,
    sort          INT          NOT NULL,
    is_active     TINYINT(1)   NOT NULL DEFAULT 1,
    created_at    DATETIME     NOT NULL,
    updated_at    DATETIME     NOT NULL,
    INDEX idx_food_type_category_active (is_active, sort)
);

CREATE TABLE PLACE_FOOD_TYPE
(
    id                          BIGINT AUTO_INCREMENT PRIMARY KEY,
    place_id                    BIGINT NOT NULL,
    place_food_type_category_id BIGINT NOT NULL,
    UNIQUE KEY uk_place_food_type (place_id, place_food_type_category_id),
    INDEX idx_place_food_type_place_id (place_id)
);

CREATE TABLE PLACE_PHOTO_CATEGORY
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    created_at DATETIME     NOT NULL,
    updated_at DATETIME     NOT NULL
);

CREATE TABLE PLACE_PHOTO_CATEGORY_IMAGE
(
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    place_photo_category_id BIGINT       NOT NULL,
    image_url               VARCHAR(255) NOT NULL,
    sort                    INT          NOT NULL,
    created_at              DATETIME     NOT NULL,
    updated_at              DATETIME     NOT NULL,
    INDEX idx_place_photo_category_image_category_id (place_photo_category_id)
);

CREATE TABLE PLACE_BANNER_IMAGE
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    place_id   BIGINT       NOT NULL,
    image_url  VARCHAR(255) NOT NULL,
    sort       INT,
    INDEX idx_place_banner_image_place_id (place_id)
);

CREATE TABLE PLACE_STATION
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    station_name VARCHAR(255) NOT NULL
);

CREATE TABLE REVIEW
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    place_id          BIGINT     NOT NULL,
    product_id        BIGINT     NOT NULL,
    member_id         BIGINT     NOT NULL,
    content           TEXT       NOT NULL,
    total_rating      DOUBLE     NOT NULL,
    taste_rating      DOUBLE,
    amount_rating     DOUBLE,
    price_rating      DOUBLE,
    atmosphere_rating DOUBLE,
    kindness_rating   DOUBLE,
    hygiene_rating    DOUBLE,
    will_revisit      TINYINT(1),
    is_hidden         TINYINT(1) NOT NULL DEFAULT 0,
    created_at        DATETIME   NOT NULL,
    updated_at        DATETIME   NOT NULL,
    INDEX idx_review_product_id (product_id)
);

CREATE TABLE REVIEW_COMMENT
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    review_id  BIGINT     NOT NULL,
    member_id  BIGINT     NOT NULL,
    content    TEXT       NOT NULL,
    is_hidden  TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME   NOT NULL,
    updated_at DATETIME   NOT NULL,
    INDEX idx_review_comment_review_id (review_id),
    INDEX idx_review_comment_member_id (member_id)
);

CREATE TABLE REVIEW_IMAGE
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    review_id  BIGINT       NOT NULL,
    image_url  VARCHAR(255) NOT NULL,
    sort       INT          NOT NULL,
    created_at DATETIME     NOT NULL,
    updated_at DATETIME     NOT NULL
);

CREATE TABLE REVIEW_LIKE
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    review_id  BIGINT   NOT NULL,
    member_id  BIGINT   NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    UNIQUE KEY uk_review_like (review_id, member_id),
    INDEX idx_review_like_review_id (review_id),
    INDEX idx_review_like_member_id (member_id)
);

CREATE TABLE REVIEW_PRODUCT
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    review_id  BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    INDEX idx_review_product_review_id (review_id),
    INDEX idx_review_product_product_id (product_id),
    UNIQUE KEY uk_review_product (review_id, product_id)
);

CREATE TABLE REVIEW_REPLY
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    comment_id         BIGINT     NOT NULL,
    member_id          BIGINT     NOT NULL,
    reply_to_member_id BIGINT,
    content            TEXT       NOT NULL,
    is_hidden          TINYINT(1) NOT NULL DEFAULT 0,
    created_at         DATETIME   NOT NULL,
    updated_at         DATETIME   NOT NULL,
    INDEX idx_review_reply_comment_id (comment_id),
    INDEX idx_review_reply_member_id (member_id)
);

CREATE TABLE REVIEW_TAG
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    review_id BIGINT NOT NULL,
    tag_id    BIGINT NOT NULL,
    INDEX idx_review_tag_review_id (review_id),
    INDEX idx_review_tag_tag_id (tag_id)
);

CREATE TABLE TAG
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    tag_name VARCHAR(255) NOT NULL
);
