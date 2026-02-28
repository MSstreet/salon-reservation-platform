-- =============================================
-- V1: 전체 테이블 생성 (FK 의존 순서)
-- =============================================

-- 1. store (루트 테이블)
CREATE TABLE store (
    store_id   BIGINT       NOT NULL AUTO_INCREMENT,
    name       VARCHAR(100) NOT NULL,
    status     VARCHAR(20)  NOT NULL,
    timezone   VARCHAR(50)  NOT NULL,
    created_at DATETIME(6)  NOT NULL,
    updated_at DATETIME(6)  NOT NULL,
    PRIMARY KEY (store_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_store_status ON store (status);

-- 2. staff (FK: store)
CREATE TABLE staff (
    staff_id   BIGINT      NOT NULL AUTO_INCREMENT,
    store_id   BIGINT      NOT NULL,
    name       VARCHAR(50) NOT NULL,
    role       VARCHAR(20) NOT NULL,
    status     VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (staff_id),
    CONSTRAINT fk_staff_store FOREIGN KEY (store_id) REFERENCES store (store_id),
    CONSTRAINT uq_staff_store_name UNIQUE (store_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_staff_store ON staff (store_id);

-- 3. service_product (FK: store)
CREATE TABLE service_product (
    product_id   BIGINT       NOT NULL AUTO_INCREMENT,
    store_id     BIGINT       NOT NULL,
    name         VARCHAR(100) NOT NULL,
    duration_min INT          NOT NULL,
    price        INT          NOT NULL,
    status       VARCHAR(20)  NOT NULL,
    created_at   DATETIME(6)  NOT NULL,
    updated_at   DATETIME(6)  NOT NULL,
    PRIMARY KEY (product_id),
    CONSTRAINT fk_product_store FOREIGN KEY (store_id) REFERENCES store (store_id),
    CONSTRAINT uq_product_store_name UNIQUE (store_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_product_store ON service_product (store_id);

-- 4. staff_schedule (FK: store, staff)
CREATE TABLE staff_schedule (
    schedule_id BIGINT      NOT NULL AUTO_INCREMENT,
    store_id    BIGINT      NOT NULL,
    staff_id    BIGINT      NOT NULL,
    date        DATE        NOT NULL,
    start_time  TIME(6)     NOT NULL,
    end_time    TIME(6)     NOT NULL,
    type        VARCHAR(10) NOT NULL,
    created_at  DATETIME(6) NOT NULL,
    updated_at  DATETIME(6) NOT NULL,
    PRIMARY KEY (schedule_id),
    CONSTRAINT fk_schedule_store FOREIGN KEY (store_id) REFERENCES store (store_id),
    CONSTRAINT fk_schedule_staff FOREIGN KEY (staff_id) REFERENCES staff (staff_id),
    CONSTRAINT uq_schedule_staff_date UNIQUE (staff_id, date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_schedule_store_date ON staff_schedule (store_id, date);
CREATE INDEX idx_schedule_staff_date ON staff_schedule (staff_id, date);

-- 5. policy_version (FK: store)
CREATE TABLE policy_version (
    policy_version_id BIGINT      NOT NULL AUTO_INCREMENT,
    store_id          BIGINT      NOT NULL,
    version           INT         NOT NULL,
    effective_from    DATETIME(6),
    rules_json        TEXT        NOT NULL,
    created_by        VARCHAR(100),
    created_at        DATETIME(6) NOT NULL,
    PRIMARY KEY (policy_version_id),
    CONSTRAINT fk_policy_store FOREIGN KEY (store_id) REFERENCES store (store_id),
    CONSTRAINT uq_policy_store_version UNIQUE (store_id, version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_policy_store_effective ON policy_version (store_id, effective_from);

-- 6. time_slot (FK: store, staff)
CREATE TABLE time_slot (
    slot_id    BIGINT      NOT NULL AUTO_INCREMENT,
    store_id   BIGINT      NOT NULL,
    staff_id   BIGINT      NOT NULL,
    date       DATE        NOT NULL,
    start_at   DATETIME(6) NOT NULL,
    end_at     DATETIME(6) NOT NULL,
    status     VARCHAR(10) NOT NULL,
    held_until DATETIME(6),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (slot_id),
    CONSTRAINT fk_slot_store FOREIGN KEY (store_id) REFERENCES store (store_id),
    CONSTRAINT fk_slot_staff FOREIGN KEY (staff_id) REFERENCES staff (staff_id),
    CONSTRAINT uq_slot_staff_start UNIQUE (staff_id, start_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_slot_store_date ON time_slot (store_id, date);
CREATE INDEX idx_slot_staff_start ON time_slot (staff_id, start_at);
CREATE INDEX idx_slot_store_staff_start ON time_slot (store_id, staff_id, start_at);

-- 7. reservation (FK: store, staff, service_product, time_slot, policy_version)
CREATE TABLE reservation (
    reservation_id    BIGINT       NOT NULL AUTO_INCREMENT,
    store_id          BIGINT       NOT NULL,
    staff_id          BIGINT       NOT NULL,
    product_id        BIGINT       NOT NULL,
    slot_id           BIGINT,
    policy_version_id BIGINT       NOT NULL,
    customer_name     VARCHAR(50)  NOT NULL,
    customer_phone_hash VARCHAR(255),
    start_at          DATETIME(6)  NOT NULL,
    end_at            DATETIME(6)  NOT NULL,
    status            VARCHAR(20)  NOT NULL,
    cancel_reason     VARCHAR(500),
    created_at        DATETIME(6)  NOT NULL,
    updated_at        DATETIME(6)  NOT NULL,
    PRIMARY KEY (reservation_id),
    CONSTRAINT fk_res_store   FOREIGN KEY (store_id)          REFERENCES store (store_id),
    CONSTRAINT fk_res_staff   FOREIGN KEY (staff_id)          REFERENCES staff (staff_id),
    CONSTRAINT fk_res_product FOREIGN KEY (product_id)        REFERENCES service_product (product_id),
    CONSTRAINT fk_res_slot    FOREIGN KEY (slot_id)           REFERENCES time_slot (slot_id),
    CONSTRAINT fk_res_policy  FOREIGN KEY (policy_version_id) REFERENCES policy_version (policy_version_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_res_store_start ON reservation (store_id, start_at);
CREATE INDEX idx_res_store_status_start ON reservation (store_id, status, start_at);
CREATE INDEX idx_res_staff_start ON reservation (staff_id, start_at);
CREATE INDEX idx_res_customer_phone ON reservation (customer_phone_hash);

-- 8. penalty (FK: store, reservation, policy_version)
CREATE TABLE penalty (
    penalty_id        BIGINT      NOT NULL AUTO_INCREMENT,
    store_id          BIGINT      NOT NULL,
    reservation_id    BIGINT      NOT NULL,
    policy_version_id BIGINT      NOT NULL,
    type              VARCHAR(20) NOT NULL,
    rate_percent      INT         NOT NULL,
    amount            INT         NOT NULL,
    currency          VARCHAR(10) NOT NULL,
    calculated_at     DATETIME(6) NOT NULL,
    basis_json        TEXT,
    PRIMARY KEY (penalty_id),
    CONSTRAINT fk_penalty_store       FOREIGN KEY (store_id)          REFERENCES store (store_id),
    CONSTRAINT fk_penalty_reservation FOREIGN KEY (reservation_id)    REFERENCES reservation (reservation_id),
    CONSTRAINT fk_penalty_policy      FOREIGN KEY (policy_version_id) REFERENCES policy_version (policy_version_id),
    CONSTRAINT uq_penalty_reservation UNIQUE (reservation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_penalty_store_type_date ON penalty (store_id, type, calculated_at);

-- 9. reservation_history (FK: store, reservation)
CREATE TABLE reservation_history (
    history_id     BINARY(16)  NOT NULL,
    store_id       BIGINT      NOT NULL,
    reservation_id BIGINT      NOT NULL,
    event_type     VARCHAR(30) NOT NULL,
    occurred_at    DATETIME(6) NOT NULL,
    actor_type     VARCHAR(20) NOT NULL,
    actor_id       VARCHAR(100),
    payload_json   TEXT,
    PRIMARY KEY (history_id),
    CONSTRAINT fk_history_store       FOREIGN KEY (store_id)       REFERENCES store (store_id),
    CONSTRAINT fk_history_reservation FOREIGN KEY (reservation_id) REFERENCES reservation (reservation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_history_store_time ON reservation_history (store_id, occurred_at);
CREATE INDEX idx_history_res_time ON reservation_history (reservation_id, occurred_at);
CREATE INDEX idx_history_type_time ON reservation_history (event_type, occurred_at);
