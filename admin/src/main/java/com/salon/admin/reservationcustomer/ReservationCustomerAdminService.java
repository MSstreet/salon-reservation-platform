package com.salon.admin.reservationcustomer;

import com.salon.admin.reservationcustomer.dto.ReservationCustomerResponse;
import com.salon.core.common.exception.BusinessException;
import com.salon.core.common.exception.ErrorCode;
import com.salon.core.domain.entity.ReservationCustomer;
import com.salon.core.domain.repository.ReservationCustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationCustomerAdminService {

    private final ReservationCustomerRepository reservationCustomerRepository;

    public ReservationCustomerResponse getById(Long id) {
        ReservationCustomer customer = reservationCustomerRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_CUSTOMER_NOT_FOUND));
        return ReservationCustomerResponse.from(customer);
    }

    public List<ReservationCustomerResponse> getAll() {
        return reservationCustomerRepository.findAll().stream()
                .map(ReservationCustomerResponse::from)
                .toList();
    }
}
