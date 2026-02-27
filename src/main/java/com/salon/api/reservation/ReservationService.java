package com.salon.api.reservation;

import com.salon.api.reservation.dto.ReservationCreateRequest;
import com.salon.api.reservation.dto.ReservationResponse;
import com.salon.common.exception.BusinessException;
import com.salon.common.exception.ErrorCode;
import com.salon.domain.entity.*;
import com.salon.domain.repository.*;
import com.salon.infrastructure.lock.RedisLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final StoreRepository storeRepository;
    private final StaffRepository staffRepository;
    private final ServiceProductRepository serviceProductRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final PolicyVersionRepository policyVersionRepository;
    private final RedisLockService redisLockService;
    private final ReservationTransactionService transactionService;

    private static final long LOCK_WAIT_TIME_MS = 5_000;
    private static final long LOCK_LEASE_TIME_MS = 10_000;

    public ReservationResponse createReservation(Long storeId, ReservationCreateRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        Staff staff = staffRepository.findById(request.staffId())
                .orElseThrow(() -> new BusinessException(ErrorCode.STAFF_NOT_FOUND));

        ServiceProduct product = serviceProductRepository.findById(request.productId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        TimeSlot slot = timeSlotRepository.findByIdAndStoreId(request.slotId(), storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SLOT_NOT_FOUND));

        PolicyVersion policy = policyVersionRepository
                .findLatestEffectivePolicy(storeId, LocalDateTime.now())
                .orElseThrow(() -> new BusinessException(ErrorCode.POLICY_NOT_FOUND));

        validateCrossReferences(store, staff, product, slot);

        String lockKey = "salon:slot-lock:" + slot.getStaff().getId() + ":" + slot.getStartAt();
        String ownerValue = redisLockService.tryLock(lockKey, LOCK_WAIT_TIME_MS, LOCK_LEASE_TIME_MS);

        if (ownerValue == null) {
            throw new BusinessException(ErrorCode.SLOT_LOCK_FAILURE);
        }

        try {
            Reservation reservation = transactionService.execute(
                    store, staff, product, request.slotId(), storeId,
                    policy, request.customerName(), request.customerPhone()
            );
            return ReservationResponse.from(reservation);
        } finally {
            redisLockService.unlock(lockKey, ownerValue);
        }
    }

    private void validateCrossReferences(Store store, Staff staff, ServiceProduct product, TimeSlot slot) {
        if (!staff.getStore().getId().equals(store.getId())) {
            throw new BusinessException(ErrorCode.STAFF_STORE_MISMATCH);
        }
        if (!product.getStore().getId().equals(store.getId())) {
            throw new BusinessException(ErrorCode.PRODUCT_STORE_MISMATCH);
        }
        if (!slot.getStore().getId().equals(store.getId())) {
            throw new BusinessException(ErrorCode.SLOT_STORE_MISMATCH);
        }
        if (!slot.getStaff().getId().equals(staff.getId())) {
            throw new BusinessException(ErrorCode.SLOT_STAFF_MISMATCH);
        }
    }
}
