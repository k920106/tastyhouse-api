INSERT INTO PLACE_AMENITY_CATEGORY
(amenity, display_name, image_url_on, image_url_off, sort, is_active, created_at, updated_at)
VALUES ('PARKING', '주차', '/images/place/icon-parking-on.png', '/images/place/icon-parking-off.png', 1, 1, NOW(), NOW()),
       ('RESTROOM', '내부화장실', '/images/place/icon-toilet-on.png', '/images/place/icon-toilet-off.png', 2, 1, NOW(),
        NOW()),
       ('RESERVATION', '예약', '/images/place/icon-reservation-on.png', '/images/place/icon-reservation-off.png', 3, 1,
        NOW(), NOW()),
       ('BABY_CHAIR', '아기의자', '/images/place/icon-baby-chair-on.png', '/images/place/icon-baby-chair-off.png', 4, 1,
        NOW(), NOW()),
       ('PET_FRIENDLY', '애견동반', '/images/place/icon-pet-on.png', '/images/place/icon-pet-off.png', 5, 1, NOW(), NOW()),
       ('OUTLET', '개별 콘센트', '/images/place/icon-socket-on.png', '/images/place/icon-socket-off.png', 6, 1, NOW(),
        NOW()),
       ('TAKEOUT', '포장', '/images/place/icon-takeout-on.png', '/images/place/icon-takeout-off.png', 7, 1, NOW(), NOW()),
       ('DELIVERY', '배달', '/images/place/icon-delivery-on.png', '/images/place/icon-delivery-off.png', 8, 1, NOW(),
        NOW());

INSERT INTO PLACE_FOOD_TYPE_CATEGORY
(food_type, display_name, image_url, sort, is_active, created_at, updated_at)
VALUES ('KOREAN', '한식', '/images/place/icon-filter-korean.png', 1, 1, NOW(), NOW()),
       ('JAPANESE', '일식', '/images/place/icon-filter-japanese.png', 2, 1, NOW(), NOW()),
       ('WESTERN', '양식', '/images/place/icon-filter-western.png', 3, 1, NOW(), NOW()),
       ('CHINESE', '중식', '/images/place/icon-filter-chinese.png', 4, 1, NOW(), NOW()),
       ('WORLD', '세계음식', '/images/place/icon-filter-world.png', 5, 1, NOW(), NOW()),
       ('SNACK', '분식', '/images/place/icon-filter-bunsik.png', 6, 1, NOW(), NOW()),
       ('BAR', '주점', '/images/place/icon-filter-pub.png', 7, 1, NOW(), NOW()),
       ('CAFE', '카페', '/images/place/icon-filter-cafe.png', 8, 1, NOW(), NOW());
