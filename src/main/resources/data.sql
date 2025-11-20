-- ** 1. 사용자 (USERS) 더미 데이터 **
-- ID는 10부터 시작.

INSERT INTO users (id, created_at, updated_at, username, tag, password_hash, bio, avatar_url, mobile)
VALUES (10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'alice_traveler', 'alice1234', '$2a$10$hashedpassword1',
        '여행 기록 마스터. 서울과 제주를 사랑하는 유저.', 'https://placehold.co/100x100/1e90ff/ffffff?text=A', '010-1111-1111'),
       (11, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'bob_photographer', 'bob1234', '$2a$10$hashedpassword2',
        '사진 전문 에디터. 여행 사진 찍는 게 취미.', 'https://placehold.co/100x100/32cd32/ffffff?text=B', '010-2222-2222'),
       (12, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'charlie_viewer', 'charlie1234', '$2a$10$hashedpassword3',
        '비디오 감상 전문. 조용히 여행을 즐기는 유저.', 'https://placehold.co/100x100/ff69b4/ffffff?text=C', '010-3333-3333');
-- ** 1-2. 추가 유저 (13, 14) **
INSERT INTO users (id, created_at, updated_at, username, tag, password_hash, bio, avatar_url, mobile)
VALUES (13, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(),
        'daisy_traveler', 'daisy1234',
        '$2a$10$hashedpassword4',
        'Alice와 함께 여행을 다니는 친구 Daisy.',
        'https://placehold.co/100x100/f0e68c/000000?text=D',
        '010-4444-4444'),

       (14, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(),
        'ethan_explorer', 'ethan1234',
        '$2a$10$hashedpassword5',
        '새로운 도시 탐험을 좋아하는 친구 Ethan.',
        'https://placehold.co/100x100/ffa500/000000?text=E',
        '010-5555-5555');



-- ** 2. 장소 (PLACES) 더미 데이터 (계층 구조 반영) **
-- ID는 10부터 시작. (PLACE_ID 참조를 위해 100이 아닌 10번대 사용)
INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'South Korea', 'COUNTRY', NULL, 35.9078, 127.7669); -- 대한민국

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (11, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Seoul', 'CITY', 10, 37.5665, 126.9780); -- 서울

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (12, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Namsan Tower', 'SPOT', 11, 37.5512, 126.9882); -- 남산타워

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (13, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Jeju Island', 'REGION', 10, 33.4893, 126.4983);
-- 제주도

-- === 해외 장소 더미 데이터 확장 ===

-- 일본 (Japan)
INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (14, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Japan', 'COUNTRY', NULL, 36.2048, 138.2529);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (15, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Tokyo', 'CITY', 14, 35.6895, 139.6917);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (16, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Shibuya Crossing', 'SPOT', 15, 35.6595, 139.7005);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (17, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Kyoto', 'CITY', 14, 35.0116, 135.7681);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (18, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Fushimi Inari Shrine', 'SPOT', 17, 34.9671, 135.7727);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (19, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Osaka', 'CITY', 14, 34.6937, 135.5023);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (20, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Dotonbori Canal', 'SPOT', 19, 34.6687, 135.5011);

-- 미국 (United States)
INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (21, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'United States', 'COUNTRY', NULL, 37.0902, -95.7129);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (22, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'New York City', 'CITY', 21, 40.7128, -74.0060);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (23, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Times Square', 'SPOT', 22, 40.7580, -73.9855);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (24, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'San Francisco', 'CITY', 21, 37.7749, -122.4194);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (25, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Golden Gate Bridge', 'SPOT', 24, 37.8199, -122.4783);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (26, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Los Angeles', 'CITY', 21, 34.0522, -118.2437);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (27, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Santa Monica Pier', 'SPOT', 26, 34.0094, -118.4973);

-- 프랑스 (France)
INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (28, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'France', 'COUNTRY', NULL, 46.2276, 2.2137);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (29, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Paris', 'CITY', 28, 48.8566, 2.3522);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (30, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Eiffel Tower', 'SPOT', 29, 48.8584, 2.2945);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (31, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Louvre Museum', 'SPOT', 29, 48.8606, 2.3376);

-- 영국 (United Kingdom)
INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (32, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'United Kingdom', 'COUNTRY', NULL, 55.3781, -3.4360);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (33, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'London', 'CITY', 32, 51.5074, -0.1278);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (34, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Big Ben', 'SPOT', 33, 51.5007, -0.1246);

-- 이탈리아 (Italy)
INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (35, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Italy', 'COUNTRY', NULL, 41.8719, 12.5674);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (36, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Rome', 'CITY', 35, 41.9028, 12.4964);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (37, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Colosseum', 'SPOT', 36, 41.8902, 12.4922);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (38, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Venice', 'CITY', 35, 45.4408, 12.3155);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (39, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Piazza San Marco', 'SPOT', 38, 45.4340, 12.3380);

-- 스페인 (Spain)
INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (40, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Spain', 'COUNTRY', NULL, 40.4637, -3.7492);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (41, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Barcelona', 'CITY', 40, 41.3851, 2.1734);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (42, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Sagrada Familia', 'SPOT', 41, 41.4036, 2.1744);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (43, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Madrid', 'CITY', 40, 40.4168, -3.7038);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (44, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Plaza Mayor', 'SPOT', 43, 40.4154, -3.7074);

-- 태국 (Thailand)
INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (45, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Thailand', 'COUNTRY', NULL, 15.8700, 100.9925);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (46, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Bangkok', 'CITY', 45, 13.7563, 100.5018);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (47, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Grand Palace', 'SPOT', 46, 13.7500, 100.4913);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (48, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Phuket Island', 'REGION', 45, 7.9519, 98.3381);

-- 베트남 (Vietnam)
INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (49, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Vietnam', 'COUNTRY', NULL, 14.0583, 108.2772);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (50, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Hanoi', 'CITY', 49, 21.0278, 105.8342);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (51, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Hoan Kiem Lake', 'SPOT', 50, 21.0285, 105.8542);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (52, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Ho Chi Minh City', 'CITY', 49, 10.8231, 106.6297);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (53, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Ben Thanh Market', 'SPOT', 52, 10.7723, 106.6984);

-- 호주 (Australia)
INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (54, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Australia', 'COUNTRY', NULL, -25.2744, 133.7751);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (55, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Sydney', 'CITY', 54, -33.8688, 151.2093);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (56, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Sydney Opera House', 'SPOT', 55, -33.8568, 151.2153);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (57, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Melbourne', 'CITY', 54, -37.8136, 144.9631);

-- 캐나다 (Canada)
INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (58, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Canada', 'COUNTRY', NULL, 56.1304, -106.3468);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (59, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Vancouver', 'CITY', 58, 49.2827, -123.1207);

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng)
VALUES (60, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Stanley Park', 'SPOT', 59, 49.3043, -123.1443);



-- ** 3. 여행 (TRIPS) 더미 데이터 **
-- ID는 10부터 시작.
-- 3-1. Alice의 현재 서울 여행 (ACTIVE, FRIENDS 공개)
INSERT INTO trips (id, created_at, updated_at, owner_id, title, description, visibility, status, start_date, end_date,
                   place_id, cover_media_id, invite_code)
VALUES (20, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 10, '2025 서울 랜드마크 여행', '남산타워와 한강을 중심으로 한 5일간의 여행', 'FRIENDS',
        'ACTIVE', DATE '2025-10-15', DATE '2025-10-20', 11, NULL, 'SEOULTRIP');

-- 3-2. Alice의 과거 제주 여행 (COMPLETED, PRIVATE 비공개)
INSERT INTO trips (id, created_at, updated_at, owner_id, title, description, visibility, status, start_date, end_date,
                   place_id, cover_media_id, invite_code)
VALUES (21, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 10, '2024 제주 힐링 여행', '혼자 떠났던 조용하고 평화로운 제주 여행 기록.', 'PRIVATE',
        'COMPLETED', DATE '2024-05-05', DATE '2024-05-10', 13, NULL, NULL);

-- 3-3. Bob의 다가오는 부산 여행 (UPCOMING, FRIENDS 공개)
INSERT INTO trips (id, created_at, updated_at, owner_id, title, description, visibility, status, start_date, end_date,
                   place_id, cover_media_id, invite_code)
VALUES (22, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 11, '2026 부산 불꽃축제', '친구들과 함께 가는 부산 여행.', 'FRIENDS', 'UPCOMING',
        DATE '2026-10-20', DATE '2026-10-23', 10, NULL, 'BUSAN2026');


-- ** 4. 미디어 에셋 (MEDIA_ASSETS) 더미 데이터 **
-- ID는 10부터 시작.
-- 4-1. Alice의 서울 사진 (PHOTO, NORMAL, Trip 20)
INSERT INTO media_assets (id, created_at, updated_at, uploader_id, trip_id, content_type, media_kind, capture_type,
                          comment, url, thumbnail_url, width, height, taken_at, is_shared_in_album)
VALUES (30, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 10, 20, 'PHOTO', 'PHOTO', 'NORMAL', '남산타워에서 찍은 서울 전경!',
        'https://cdn.trip.com/img/namsan_view.jpg', 'https://cdn.trip.com/thumb/namsan_view.jpg', 1920, 1080,
        CURRENT_TIMESTAMP(), TRUE);

-- 4-2. Bob의 제주도 영상 (VIDEO, VIDEO, Trip 21)
INSERT INTO media_assets (id, created_at, updated_at, uploader_id, trip_id, content_type, media_kind, capture_type,
                          comment, url, thumbnail_url, width, height, duration_sec, taken_at, is_shared_in_album)
VALUES (31, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 11, 21, 'VIDEO', 'VIDEO', 'VIDEO', '드론으로 찍은 제주 해변!',
        'https://cdn.trip.com/video/jeju_drone.mp4', 'https://cdn.trip.com/thumb/jeju_drone.jpg', 1280, 720, 45,
        CURRENT_TIMESTAMP(), FALSE);

-- 4-3. Charlie의 필름 사진 (PHOTO, FILM, Trip 20)
INSERT INTO media_assets (id, created_at, updated_at, uploader_id, trip_id, content_type, media_kind, capture_type,
                          comment, url, thumbnail_url, width, height, taken_at, is_shared_in_album)
VALUES (32, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 12, 20, 'PHOTO', 'PHOTO', 'FILM', '갬성 가득한 필름 카메라 사진',
        'https://cdn.trip.com/img/film_pic.jpg', 'https://cdn.trip.com/thumb/film_pic.jpg', 1000, 1000,
        CURRENT_TIMESTAMP(), TRUE);

-- (참고: Trip 200의 cover_media_id를 30으로 업데이트 - 순환 참조를 피하기 위해 INSERT 후 UPDATE)
UPDATE trips
SET cover_media_id = 30
WHERE id = 20;


-- ** 5. 포스트 (POSTS) 더미 데이터 **
-- ID는 10부터 시작.
-- 5-1. Alice의 포스트 (Trip 20, Namsan Tower)
INSERT INTO posts (id, created_at, updated_at, author_id, trip_id, caption, visibility, place_id)
VALUES (40, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 10, 20, '여행샷 첫 게시물! 남산타워에서 보는 서울은 언제나 감동이야.', 'FRIENDS', 12);

-- 5-2. Bob의 포스트 (Trip 20, Seoul)
INSERT INTO posts (id, created_at, updated_at, author_id, trip_id, caption, visibility, place_id)
VALUES (41, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 11, 20, '최고의 포토존 발견! Alice와 함께하는 서울 여행 📸', 'FRIENDS', 11);

-- 5-3. Alice의 제주 비공개 포스트 (Trip 21, Jeju Island)
INSERT INTO posts (id, created_at, updated_at, author_id, trip_id, caption, visibility, place_id)
VALUES (42, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 10, 21, '제주도의 조용한 해변. 나만의 힐링 기록.', 'PRIVATE', 13);


-- ** 6. 포스트 미디어 (POST_MEDIA) 연결 더미 데이터 **
-- ID는 10부터 시작.
-- Post 40 (Alice's Namsan post)에 Media 30 (Alice's photo) 연결
INSERT INTO post_media (id, created_at, updated_at, post_id, object_type, object_id, position)
VALUES (50, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 40, 'MEDIA', 30, 1);

-- Post 41 (Bob's Seoul post)에 Media 32 (Charlie's film photo) 연결
INSERT INTO post_media (id, created_at, updated_at, post_id, object_type, object_id, position)
VALUES (51, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 41, 'MEDIA', 32, 1);

-- Post 42 (Alice's Jeju post)에 Media 31 (Bob's video) 연결
INSERT INTO post_media (id, created_at, updated_at, post_id, object_type, object_id, position)
VALUES (52, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 42, 'MEDIA', 31, 1);

-- ** 7. 여행 참가자 (TRIP_PARTICIPANTS) 더미 데이터 **
-- ID는 60부터 시작 (다른 테이블과 숫자대역 구분용)

INSERT INTO trip_participants (id, created_at, updated_at, trip_id, user_id, role)
VALUES (60, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 20, 13, 'VIEWER'),
       (61, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 20, 14, 'VIEWER'),
       (62, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 20, 10, 'OWNER');
