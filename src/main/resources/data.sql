-- ** 1. 사용자 (USERS) 더미 데이터 **
-- ID는 10부터 시작.
INSERT INTO users (id, created_at, updated_at, username, tag, password_hash, bio, avatar_url, mobile) VALUES
                                                                                                            (10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'alice_traveler', 'alice@trip.com', '$2a$10$hashedpassword1', '여행 기록 마스터. 서울과 제주를 사랑하는 유저.', 'https://placehold.co/100x100/1e90ff/ffffff?text=A', '010-1111-1111'),
                                                                                                            (11, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'bob_photographer', 'bob@trip.com', '$2a$10$hashedpassword2', '사진 전문 에디터. 여행 사진 찍는 게 취미.', 'https://placehold.co/100x100/32cd32/ffffff?text=B', '010-2222-2222'),
                                                                                                            (12, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'charlie_viewer', 'charlie@trip.com', '$2a$10$hashedpassword3', '비디오 감상 전문. 조용히 여행을 즐기는 유저.', 'https://placehold.co/100x100/ff69b4/ffffff?text=C', '010-3333-3333');


-- ** 2. 장소 (PLACES) 더미 데이터 (계층 구조 반영) **
-- ID는 10부터 시작. (PLACE_ID 참조를 위해 100이 아닌 10번대 사용)
INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng) VALUES
    (10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'South Korea', 'COUNTRY', NULL, 35.9078, 127.7669); -- 대한민국

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng) VALUES
    (11, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Seoul', 'CITY', 10, 37.5665, 126.9780); -- 서울

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng) VALUES
    (12, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Namsan Tower', 'SPOT', 11, 37.5512, 126.9882); -- 남산타워

INSERT INTO places (id, created_at, updated_at, name, type, parent_id, lat, lng) VALUES
    (13, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'Jeju Island', 'REGION', 10, 33.4893, 126.4983); -- 제주도


-- ** 3. 여행 (TRIPS) 더미 데이터 **
-- ID는 10부터 시작.
-- 3-1. Alice의 현재 서울 여행 (ACTIVE, FRIENDS 공개)
INSERT INTO trips (id, created_at, updated_at, owner_id, title, description, visibility, status, start_date, end_date, place_id, cover_media_id, invite_code) VALUES
    (20, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 10, '2025 서울 랜드마크 여행', '남산타워와 한강을 중심으로 한 5일간의 여행', 'FRIENDS', 'ACTIVE', DATE '2025-10-15', DATE '2025-10-20', 11, NULL, 'SEOULTRIP');

-- 3-2. Alice의 과거 제주 여행 (COMPLETED, PRIVATE 비공개)
INSERT INTO trips (id, created_at, updated_at, owner_id, title, description, visibility, status, start_date, end_date, place_id, cover_media_id, invite_code) VALUES
    (21, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 10, '2024 제주 힐링 여행', '혼자 떠났던 조용하고 평화로운 제주 여행 기록.', 'PRIVATE', 'COMPLETED', DATE '2024-05-05', DATE '2024-05-10', 13, NULL, NULL);

-- 3-3. Bob의 다가오는 부산 여행 (UPCOMING, FRIENDS 공개)
INSERT INTO trips (id, created_at, updated_at, owner_id, title, description, visibility, status, start_date, end_date, place_id, cover_media_id, invite_code) VALUES
    (22, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 11, '2026 부산 불꽃축제', '친구들과 함께 가는 부산 여행.', 'FRIENDS', 'UPCOMING', DATE '2026-10-20', DATE '2026-10-23', 10, NULL, 'BUSAN2026');


-- ** 4. 미디어 에셋 (MEDIA_ASSETS) 더미 데이터 **
-- ID는 10부터 시작.
-- 4-1. Alice의 서울 사진 (PHOTO, NORMAL, Trip 20)
INSERT INTO media_assets (id, created_at, updated_at, uploader_id, trip_id, media_kind, capture_type, comment, url, thumbnail_url, width, height, taken_at, is_shared_in_album) VALUES
    (30, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 10, 20, 'PHOTO', 'NORMAL', '남산타워에서 찍은 서울 전경!', 'https://cdn.trip.com/img/namsan_view.jpg', 'https://cdn.trip.com/thumb/namsan_view.jpg', 1920, 1080, CURRENT_TIMESTAMP(), TRUE);

-- 4-2. Bob의 제주도 영상 (VIDEO, VIDEO, Trip 21)
INSERT INTO media_assets (id, created_at, updated_at, uploader_id, trip_id, media_kind, capture_type, comment, url, thumbnail_url, width, height, duration_sec, taken_at, is_shared_in_album) VALUES
    (31, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 11, 21, 'VIDEO', 'VIDEO', '드론으로 찍은 제주 해변!', 'https://cdn.trip.com/video/jeju_drone.mp4', 'https://cdn.trip.com/thumb/jeju_drone.jpg', 1280, 720, 45, CURRENT_TIMESTAMP(), FALSE);

-- 4-3. Charlie의 필름 사진 (PHOTO, FILM, Trip 20)
INSERT INTO media_assets (id, created_at, updated_at, uploader_id, trip_id, media_kind, capture_type, comment, url, thumbnail_url, width, height, taken_at, is_shared_in_album) VALUES
    (32, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 12, 20, 'PHOTO', 'FILM', '갬성 가득한 필름 카메라 사진', 'https://cdn.trip.com/img/film_pic.jpg', 'https://cdn.trip.com/thumb/film_pic.jpg', 1000, 1000, CURRENT_TIMESTAMP(), TRUE);

-- (참고: Trip 200의 cover_media_id를 30으로 업데이트 - 순환 참조를 피하기 위해 INSERT 후 UPDATE)
UPDATE trips SET cover_media_id = 30 WHERE id = 20;


-- ** 5. 포스트 (POSTS) 더미 데이터 **
-- ID는 10부터 시작.
-- 5-1. Alice의 포스트 (Trip 20, Namsan Tower)
INSERT INTO posts (id, created_at, updated_at, author_id, trip_id, caption, visibility, place_id) VALUES
    (40, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 10, 20, '여행샷 첫 게시물! 남산타워에서 보는 서울은 언제나 감동이야.', 'FRIENDS', 12);

-- 5-2. Bob의 포스트 (Trip 20, Seoul)
INSERT INTO posts (id, created_at, updated_at, author_id, trip_id, caption, visibility, place_id) VALUES
    (41, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 11, 20, '최고의 포토존 발견! Alice와 함께하는 서울 여행 📸', 'FRIENDS', 11);

-- 5-3. Alice의 제주 비공개 포스트 (Trip 21, Jeju Island)
INSERT INTO posts (id, created_at, updated_at, author_id, trip_id, caption, visibility, place_id) VALUES
    (42, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 10, 21, '제주도의 조용한 해변. 나만의 힐링 기록.', 'PRIVATE', 13);


-- ** 6. 포스트 미디어 (POST_MEDIA) 연결 더미 데이터 **
-- ID는 10부터 시작.
-- Post 40 (Alice's Namsan post)에 Media 30 (Alice's photo) 연결
INSERT INTO post_media (id, created_at, updated_at, post_id, object_type, object_id, position) VALUES
    (50, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 40, 'MEDIA', 30, 1);

-- Post 41 (Bob's Seoul post)에 Media 32 (Charlie's film photo) 연결
INSERT INTO post_media (id, created_at, updated_at, post_id, object_type, object_id, position) VALUES
    (51, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 41, 'MEDIA', 32, 1);

-- Post 42 (Alice's Jeju post)에 Media 31 (Bob's video) 연결
INSERT INTO post_media (id, created_at, updated_at, post_id, object_type, object_id, position) VALUES
    (52, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 42, 'MEDIA', 31, 1);