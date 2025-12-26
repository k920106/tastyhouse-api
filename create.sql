
CREATE TABLE BANNER (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100),
    image_url VARCHAR(255) NOT NULL,
    link_url VARCHAR(500),
    start_date DATETIME,
    end_date DATETIME,
    sort INT NOT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE EVENT_PRIZE (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    prize_rank INT NOT NULL,
    name VARCHAR(200) NOT NULL,
    brand VARCHAR(100) NOT NULL,
    image_url VARCHAR(500),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    UNIQUE KEY uk_event_prize_rank (event_id, prize_rank),
    INDEX idx_event_prize (event_id, prize_rank),
    INDEX idx_prize_brand (brand),
    INDEX idx_prize_name (name)
);

CREATE TABLE EVENT (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    type VARCHAR(50) NOT NULL DEFAULT 'RANKING',
    status VARCHAR(20) NOT NULL,
    start_at DATETIME NOT NULL,
    end_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_event_status (status),
    INDEX idx_event_period (start_at, end_at)
);

CREATE TABLE MEMBER_REVIEW_RANK (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    review_count INT NOT NULL,
    rank_no INT NOT NULL,
    rank_type VARCHAR(20) NOT NULL,
    base_date DATE NOT NULL,
    last_review_at DATETIME,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    UNIQUE KEY uk_member_rank (member_id, rank_type, base_date),
    INDEX idx_rank_query (rank_type, base_date, rank_no),
    INDEX idx_member_rank (member_id, rank_type)
);

CREATE TABLE PRODUCT (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    place_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    image_url VARCHAR(255),
    price INT NOT NULL,
    discount_price INT,
    discount_rate DECIMAL(19, 2),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE REVIEW (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    place_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    total_rating DOUBLE NOT NULL,
    taste_rating DOUBLE,
    amount_rating DOUBLE,
    price_rating DOUBLE,
    atmosphere_rating DOUBLE,
    kindness_rating DOUBLE,
    hygiene_rating DOUBLE,
    will_revisit TINYINT(1),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE REVIEW_IMAGE (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    review_id BIGINT NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    sort INT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE MEMBER (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    birth_date INT,
    gender VARCHAR(10) NOT NULL,
    phone_number VARCHAR(20),
    member_grade VARCHAR(20) NOT NULL DEFAULT 'NEWCOMER',
    profile_image_url VARCHAR(500),
    status_message VARCHAR(200),
    push_notification_enabled TINYINT(1) NOT NULL DEFAULT 1,
    marketing_info_enabled TINYINT(1) NOT NULL DEFAULT 0,
    event_info_enabled TINYINT(1) NOT NULL DEFAULT 0,
    member_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE PLACE_IMAGE (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    place_id BIGINT NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    is_thumbnail TINYINT(1)
);

CREATE TABLE PLACE_STATION (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    station_name VARCHAR(255) NOT NULL
);

CREATE TABLE PLACE_TAG (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    place_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL
);

CREATE TABLE PLACE (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    station_id BIGINT NOT NULL,
    place_name VARCHAR(255) NOT NULL UNIQUE,
    latitude DECIMAL(9, 6) NOT NULL,
    longitude DECIMAL(9, 6) NOT NULL,
    rating DOUBLE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE TAG (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tag_name VARCHAR(255) NOT NULL
);

CREATE TABLE PLACE_CHOICE (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    place_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);
