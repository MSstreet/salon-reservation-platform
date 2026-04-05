-- V5: reservation_customer 테이블에 status 컬럼 추가
ALTER TABLE reservation_customer
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' AFTER email;