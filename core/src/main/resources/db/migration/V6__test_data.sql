-- =============================================
-- V6: 로컬 개발용 테스트 데이터
-- =============================================

-- 1. store
INSERT INTO store (name, status, timezone, created_at, updated_at)
VALUES ('헤어살롱 강남점', 'ACTIVE', 'Asia/Seoul', NOW(6), NOW(6));

-- 2. staff
INSERT INTO staff (store_id, name, role, status, created_at, updated_at) VALUES
(1, '김민준',  'DESIGNER',    'ACTIVE', NOW(6), NOW(6)),
(1, '이서연',  'DESIGNER',    'ACTIVE', NOW(6), NOW(6)),
(1, '박관리자', 'STORE_ADMIN', 'ACTIVE', NOW(6), NOW(6));

-- 3. service_menu
INSERT INTO service_menu (store_id, name, duration_min, price, status, created_at, updated_at) VALUES
(1, '커트',  30,  20000, 'ACTIVE', NOW(6), NOW(6)),
(1, '펌',    90,  80000, 'ACTIVE', NOW(6), NOW(6)),
(1, '염색', 120, 100000, 'ACTIVE', NOW(6), NOW(6));

-- 4. staff_menu (김민준: 커트/펌/염색, 이서연: 커트/염색)
INSERT INTO staff_menu (staff_id, menu_id) VALUES
(1, 1), (1, 2), (1, 3),
(2, 1), (2, 3);

-- 5. staff_schedule (오늘 ~ 6일 후)
INSERT INTO staff_schedule (store_id, staff_id, date, start_time, end_time, type, created_at, updated_at)
WITH RECURSIVE days(d) AS (
    SELECT 0 UNION ALL SELECT d + 1 FROM days WHERE d < 6
)
SELECT 1, 1, DATE_ADD(CURDATE(), INTERVAL d DAY), '09:00:00', '18:00:00', 'WORK', NOW(6), NOW(6) FROM days
UNION ALL
SELECT 1, 2, DATE_ADD(CURDATE(), INTERVAL d DAY), '10:00:00', '19:00:00', 'WORK', NOW(6), NOW(6) FROM days;

-- 6. time_slot (30분 단위, 오늘 ~ 6일 후)
-- 김민준: 09:00 ~ 18:00 (18슬롯/일)
INSERT INTO time_slot (store_id, staff_id, date, start_at, end_at, status, created_at, updated_at)
WITH RECURSIVE
    slots(n) AS (SELECT 0 UNION ALL SELECT n + 1 FROM slots WHERE n < 17),
    days(d)  AS (SELECT 0 UNION ALL SELECT d + 1 FROM days  WHERE d < 6)
SELECT
    1, 1,
    DATE_ADD(CURDATE(), INTERVAL d DAY),
    TIMESTAMPADD(MINUTE, 9 * 60 + n * 30,      CAST(DATE_ADD(CURDATE(), INTERVAL d DAY) AS DATETIME)),
    TIMESTAMPADD(MINUTE, 9 * 60 + n * 30 + 30, CAST(DATE_ADD(CURDATE(), INTERVAL d DAY) AS DATETIME)),
    'OPEN', NOW(6), NOW(6)
FROM slots, days;

-- 이서연: 10:00 ~ 19:00 (18슬롯/일)
INSERT INTO time_slot (store_id, staff_id, date, start_at, end_at, status, created_at, updated_at)
WITH RECURSIVE
    slots(n) AS (SELECT 0 UNION ALL SELECT n + 1 FROM slots WHERE n < 17),
    days(d)  AS (SELECT 0 UNION ALL SELECT d + 1 FROM days  WHERE d < 6)
SELECT
    1, 2,
    DATE_ADD(CURDATE(), INTERVAL d DAY),
    TIMESTAMPADD(MINUTE, 10 * 60 + n * 30,      CAST(DATE_ADD(CURDATE(), INTERVAL d DAY) AS DATETIME)),
    TIMESTAMPADD(MINUTE, 10 * 60 + n * 30 + 30, CAST(DATE_ADD(CURDATE(), INTERVAL d DAY) AS DATETIME)),
    'OPEN', NOW(6), NOW(6)
FROM slots, days;

-- 7. reservation_customer
INSERT INTO reservation_customer (name, phone, phone_hash, email, status, created_at, updated_at)
VALUES ('홍길동', '010-1234-5678', SHA2('010-1234-5678', 256), 'hong@example.com', 'ACTIVE', NOW(6), NOW(6));

-- 8. reservation (내일 10:00, 김민준, 커트, CONFIRMED)
INSERT INTO reservation (store_id, staff_id, menu_id, slot_id, reservation_customer_id,
                         customer_name, customer_phone_hash, start_at, end_at, status, created_at, updated_at)
SELECT
    1, 1, 1,
    (SELECT slot_id FROM time_slot
     WHERE staff_id = 1
       AND start_at = TIMESTAMPADD(MINUTE, 10 * 60, CAST(DATE_ADD(CURDATE(), INTERVAL 1 DAY) AS DATETIME))),
    1,
    '홍길동',
    SHA2('010-1234-5678', 256),
    TIMESTAMPADD(MINUTE, 10 * 60,      CAST(DATE_ADD(CURDATE(), INTERVAL 1 DAY) AS DATETIME)),
    TIMESTAMPADD(MINUTE, 10 * 60 + 30, CAST(DATE_ADD(CURDATE(), INTERVAL 1 DAY) AS DATETIME)),
    'CONFIRMED',
    NOW(6), NOW(6);

-- 예약된 슬롯 BOOKED 처리
UPDATE time_slot
SET status = 'BOOKED', updated_at = NOW(6)
WHERE staff_id = 1
  AND start_at = TIMESTAMPADD(MINUTE, 10 * 60, CAST(DATE_ADD(CURDATE(), INTERVAL 1 DAY) AS DATETIME));

-- 9. deposit (예약금 결제 완료)
INSERT INTO deposit (reservation_id, amount, currency, status, paid_at, created_at)
VALUES (1, 20000, 'KRW', 'PAID', NOW(6), NOW(6));