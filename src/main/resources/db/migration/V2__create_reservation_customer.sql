-- =============================================
-- V2: reservation_customer 테이블 생성 + reservation FK 추가
-- =============================================

CREATE TABLE reservation_customer (
    reservation_customer_id BIGINT       NOT NULL AUTO_INCREMENT,
    name                    VARCHAR(50)  NOT NULL,
    phone                   VARCHAR(20)  NOT NULL,
    phone_hash              VARCHAR(255) NOT NULL,
    email                   VARCHAR(100),
    created_at              DATETIME(6)  NOT NULL,
    updated_at              DATETIME(6)  NOT NULL,
    PRIMARY KEY (reservation_customer_id),
    CONSTRAINT uq_res_customer_phone_hash UNIQUE (phone_hash)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- reservation 테이블에 reservation_customer_id 컬럼 + FK 추가
ALTER TABLE reservation
    ADD COLUMN reservation_customer_id BIGINT AFTER policy_version_id,
    ADD CONSTRAINT fk_res_customer FOREIGN KEY (reservation_customer_id) REFERENCES reservation_customer (reservation_customer_id);
