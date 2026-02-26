package com.salon.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 409 Conflict
    SLOT_CONFLICT(HttpStatus.CONFLICT, "SLOT_CONFLICT", "해당 슬롯은 이미 예약되었습니다"),

    // 503 Service Unavailable
    SLOT_LOCK_FAILURE(HttpStatus.SERVICE_UNAVAILABLE, "SLOT_LOCK_FAILURE", "슬롯 잠금 획득에 실패했습니다. 잠시 후 다시 시도해주세요"),

    // 404 Not Found
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE_NOT_FOUND", "매장을 찾을 수 없습니다"),
    STAFF_NOT_FOUND(HttpStatus.NOT_FOUND, "STAFF_NOT_FOUND", "스태프를 찾을 수 없습니다"),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND", "서비스 상품을 찾을 수 없습니다"),
    SLOT_NOT_FOUND(HttpStatus.NOT_FOUND, "SLOT_NOT_FOUND", "타임슬롯을 찾을 수 없습니다"),
    POLICY_NOT_FOUND(HttpStatus.NOT_FOUND, "POLICY_NOT_FOUND", "유효한 정책을 찾을 수 없습니다"),

    // 400 Bad Request
    STAFF_STORE_MISMATCH(HttpStatus.BAD_REQUEST, "STAFF_STORE_MISMATCH", "스태프가 해당 매장 소속이 아닙니다"),
    PRODUCT_STORE_MISMATCH(HttpStatus.BAD_REQUEST, "PRODUCT_STORE_MISMATCH", "상품이 해당 매장 소속이 아닙니다"),
    SLOT_STORE_MISMATCH(HttpStatus.BAD_REQUEST, "SLOT_STORE_MISMATCH", "슬롯이 해당 매장 소속이 아닙니다"),
    SLOT_STAFF_MISMATCH(HttpStatus.BAD_REQUEST, "SLOT_STAFF_MISMATCH", "슬롯이 해당 스태프의 것이 아닙니다"),

    // ReservationCustomer
    RESERVATION_CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION_CUSTOMER_NOT_FOUND", "예약 고객을 찾을 수 없습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
