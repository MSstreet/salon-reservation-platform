package com.salon.core.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 409 Conflict
    SLOT_CONFLICT(HttpStatus.CONFLICT, "SLOT_CONFLICT", "해당 슬롯은 이미 예약되었습니다"),
    SCHEDULE_ALREADY_EXISTS(HttpStatus.CONFLICT, "SCHEDULE_ALREADY_EXISTS", "해당 날짜에 이미 스케줄이 등록되어 있습니다"),

    // 503 Service Unavailable
    SLOT_LOCK_FAILURE(HttpStatus.SERVICE_UNAVAILABLE, "SLOT_LOCK_FAILURE", "슬롯 잠금 획득에 실패했습니다. 잠시 후 다시 시도해주세요"),

    // 404 Not Found
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE_NOT_FOUND", "매장을 찾을 수 없습니다"),
    STAFF_NOT_FOUND(HttpStatus.NOT_FOUND, "STAFF_NOT_FOUND", "스태프를 찾을 수 없습니다"),
    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "MENU_NOT_FOUND", "서비스 메뉴를 찾을 수 없습니다"),
    SLOT_NOT_FOUND(HttpStatus.NOT_FOUND, "SLOT_NOT_FOUND", "타임슬롯을 찾을 수 없습니다"),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION_NOT_FOUND", "예약을 찾을 수 없습니다"),
    DEPOSIT_NOT_FOUND(HttpStatus.NOT_FOUND, "DEPOSIT_NOT_FOUND", "예약금 정보를 찾을 수 없습니다"),

    // 400 Bad Request
    STAFF_STORE_MISMATCH(HttpStatus.BAD_REQUEST, "STAFF_STORE_MISMATCH", "스태프가 해당 매장 소속이 아닙니다"),
    MENU_STORE_MISMATCH(HttpStatus.BAD_REQUEST, "MENU_STORE_MISMATCH", "메뉴가 해당 매장 소속이 아닙니다"),
    SLOT_STORE_MISMATCH(HttpStatus.BAD_REQUEST, "SLOT_STORE_MISMATCH", "슬롯이 해당 매장 소속이 아닙니다"),
    SLOT_STAFF_MISMATCH(HttpStatus.BAD_REQUEST, "SLOT_STAFF_MISMATCH", "슬롯이 해당 스태프의 것이 아닙니다"),
    RESERVATION_CANNOT_CANCEL(HttpStatus.BAD_REQUEST, "RESERVATION_CANNOT_CANCEL", "취소할 수 없는 예약 상태입니다"),
    RESERVATION_INVALID_STATUS(HttpStatus.BAD_REQUEST, "RESERVATION_INVALID_STATUS", "유효하지 않은 예약 상태 전이입니다"),
    DEPOSIT_INVALID_STATUS(HttpStatus.BAD_REQUEST, "DEPOSIT_INVALID_STATUS", "처리할 수 없는 예약금 상태입니다"),

    // ReservationCustomer
    RESERVATION_CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION_CUSTOMER_NOT_FOUND", "예약 고객을 찾을 수 없습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}