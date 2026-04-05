-- V8: store 테이블에 address 컬럼 추가
ALTER TABLE store
    ADD COLUMN address VARCHAR(255) AFTER timezone;