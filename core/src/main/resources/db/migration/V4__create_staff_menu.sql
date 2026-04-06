-- =============================================
-- V4: staff_menu 테이블 생성 (디자이너별 시술 메뉴 매핑)
-- =============================================

CREATE TABLE staff_menu (
    staff_menu_id BIGINT NOT NULL AUTO_INCREMENT,
    staff_id      BIGINT NOT NULL,
    menu_id       BIGINT NOT NULL,
    PRIMARY KEY (staff_menu_id),
    CONSTRAINT fk_staff_menu_staff FOREIGN KEY (staff_id) REFERENCES staff (staff_id),
    CONSTRAINT fk_staff_menu_menu  FOREIGN KEY (menu_id)  REFERENCES service_menu (menu_id),
    CONSTRAINT uq_staff_menu       UNIQUE (staff_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_staff_menu_staff ON staff_menu (staff_id);
CREATE INDEX idx_staff_menu_menu  ON staff_menu (menu_id);