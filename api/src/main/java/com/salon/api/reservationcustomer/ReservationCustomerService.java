package com.salon.api.reservationcustomer;

import com.salon.api.reservationcustomer.dto.ReservationCustomerResponse;
import com.salon.core.common.exception.BusinessException;
import com.salon.core.common.exception.ErrorCode;
import com.salon.core.domain.entity.ReservationCustomer;
import com.salon.core.domain.repository.ReservationCustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationCustomerService {

    private final ReservationCustomerRepository reservationCustomerRepository;

    public ReservationCustomerResponse getById(Long id) {
        ReservationCustomer customer = reservationCustomerRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_CUSTOMER_NOT_FOUND));
        return ReservationCustomerResponse.from(customer);
    }

    @Transactional
    public ReservationCustomer getOrCreate(String name, String phone, String email) {
        String phoneHash = hashPhone(phone);
        return reservationCustomerRepository.findByPhoneHash(phoneHash)
                .orElseGet(() -> reservationCustomerRepository.save(
                        ReservationCustomer.create(name, phone, phoneHash, email)
                ));
    }

    private String hashPhone(String phone) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(phone.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
