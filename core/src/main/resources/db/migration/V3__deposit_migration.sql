-- =============================================
-- V3: Penalty/PolicyVersion 제거, ServiceProduct → ServiceMenu 변경,
--     Deposit 테이블 추가
-- =============================================

-- 1. penalty 테이블 삭제
ALTER TABLE penalty DROP FOREIGN KEY fk_penalty_reservation;
ALTER TABLE penalty DROP FOREIGN KEY fk_penalty_store;
ALTER TABLE penalty DROP FOREIGN KEY fk_penalty_policy;
DROP TABLE penalty;

-- 2. reservation 에서 policy_version_id 컬럼 제거
ALTER TABLE reservation DROP FOREIGN KEY fk_res_policy;
ALTER TABLE reservation DROP COLUMN policy_version_id;

-- 3. policy_version 테이블 삭제
ALTER TABLE policy_version DROP FOREIGN KEY fk_policy_store;
DROP TABLE policy_version;

-- 4. service_product → service_menu 테이블 이름 변경
RENAME TABLE service_product TO service_menu;

ALTER TABLE service_menu
    DROP FOREIGN KEY fk_product_store,
    DROP INDEX uq_product_store_name,
    DROP INDEX idx_product_store;

ALTER TABLE service_menu
    CHANGE COLUMN product_id menu_id BIGINT NOT NULL AUTO_INCREMENT;

ALTER TABLE service_menu
    ADD CONSTRAINT fk_menu_store FOREIGN KEY (store_id) REFERENCES store (store_id),
    ADD CONSTRAINT uq_menu_store_name UNIQUE (store_id, name);

CREATE INDEX idx_menu_store ON service_menu (store_id);

-- 5. reservation 의 product_id → menu_id FK 교체
ALTER TABLE reservation DROP FOREIGN KEY fk_res_product;

ALTER TABLE reservation
    CHANGE COLUMN product_id menu_id BIGINT NOT NULL;

ALTER TABLE reservation
    ADD CONSTRAINT fk_res_menu FOREIGN KEY (menu_id) REFERENCES service_menu (menu_id);

-- 6. deposit 테이블 생성
CREATE TABLE deposit (
    deposit_id     BIGINT      NOT NULL AUTO_INCREMENT,
    reservation_id BIGINT      NOT NULL,
    amount         INT         NOT NULL,
    currency       VARCHAR(10) NOT NULL,
    status         VARCHAR(20) NOT NULL,
    paid_at        DATETIME(6),
    refunded_at    DATETIME(6),
    forfeited_at   DATETIME(6),
    created_at     DATETIME(6) NOT NULL,
    PRIMARY KEY (deposit_id),
    CONSTRAINT fk_deposit_reservation FOREIGN KEY (reservation_id) REFERENCES reservation (reservation_id),
    CONSTRAINT uq_deposit_reservation UNIQUE (reservation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_deposit_reservation ON deposit (reservation_id);