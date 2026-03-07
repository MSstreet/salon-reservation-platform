package com.salon.api.reservationcustomer.dto;

import com.salon.core.domain.entity.ReservationCustomer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationCustomerResponse {

    private final Long id;
    private final String name;
    private final String phone;
    private final String email;
    private final LocalDateTime createdAt;

    public static ReservationCustomerResponse from(ReservationCustomer customer) {
        return new ReservationCustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getCreatedAt()
        );
    }
}
