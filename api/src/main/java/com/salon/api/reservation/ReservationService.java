package com.salon.api.reservation;

import com.salon.api.reservation.dto.ReservationCreateRequest;
import com.salon.api.reservation.dto.ReservationResponse;
import com.salon.core.common.exception.BusinessException;
import com.salon.core.common.exception.ErrorCode;
import com.salon.core.domain.entity.*;
import com.salon.core.domain.repository.*;
import com.salon.core.infrastructure.idempotency.IdempotencyService;
import com.salon.core.infrastructure.lock.RedisLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.salon.core.domain.enums.ReservationStatus;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final StoreRepository storeRepository;
    private final StaffRepository staffRepository;
    private final ServiceMenuRepository serviceMenuRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final ReservationRepository reservationRepository;
    private final RedisLockService redisLockService;
    private final IdempotencyService idempotencyService;
    private final ReservationTransactionService transactionService;

    private static final long LOCK_WAIT_TIME_MS = 5_000;
    private static final long LOCK_LEASE_TIME_MS = 10_000;

    @Transactional(readOnly = true)
    public List<ReservationResponse> getList(Long storeId, ReservationStatus status,
                                             LocalDate date, Long staffId) {
        return reservationRepository.search(storeId, status, date, staffId).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservationResponse getById(Long storeId, Long reservationId) {
        Reservation reservation = reservationRepository.findByIdAndStoreId(reservationId, storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));
        return ReservationResponse.from(reservation);
    }

    public ReservationResponse createReservation(Long storeId, ReservationCreateRequest request,
                                                 String idempotencyKey) {
        if (idempotencyKey != null) {
            return idempotencyService.getReservationId(idempotencyKey)
                    .flatMap(reservationRepository::findById)
                    .map(ReservationResponse::from)
                    .orElseGet(() -> doCreate(storeId, request, idempotencyKey));
        }
        return doCreate(storeId, request, null);
    }

    private ReservationResponse doCreate(Long storeId, ReservationCreateRequest request,
                                         String idempotencyKey) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        Staff staff = staffRepository.findById(request.staffId())
                .orElseThrow(() -> new BusinessException(ErrorCode.STAFF_NOT_FOUND));

        ServiceMenu menu = serviceMenuRepository.findByIdAndStoreId(request.menuId(), storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));

        TimeSlot firstSlot = timeSlotRepository.findByIdAndStoreId(request.slotId(), storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SLOT_NOT_FOUND));

        validateStoreReferences(store, staff, menu, firstSlot);

        List<TimeSlot> slots = resolveConsecutiveSlots(staff.getId(), storeId, firstSlot, menu);

        String lockKey = "reservation:staff:" + staff.getId() + ":start:" + firstSlot.getStartAt();
        String ownerValue = redisLockService.tryLock(lockKey, LOCK_WAIT_TIME_MS, LOCK_LEASE_TIME_MS);

        if (ownerValue == null) {
            throw new BusinessException(ErrorCode.SLOT_LOCK_FAILURE);
        }

        try {
            Reservation reservation = transactionService.execute(
                    store, staff, menu, slots,
                    request.customerName(), request.customerPhone()
            );
            if (idempotencyKey != null) {
                idempotencyService.save(idempotencyKey, reservation.getId());
            }
            return ReservationResponse.from(reservation);
        } finally {
            redisLockService.unlock(lockKey, ownerValue);
        }
    }

    private List<TimeSlot> resolveConsecutiveSlots(Long staffId, Long storeId,
                                                   TimeSlot firstSlot, ServiceMenu menu) {
        int requiredSlots = (int) Math.ceil(menu.getDurationMin() / 30.0);
        LocalDateTime endAt = firstSlot.getStartAt().plusMinutes(menu.getDurationMin());

        List<TimeSlot> slots = timeSlotRepository.findSlotsInRange(
                staffId, storeId, firstSlot.getStartAt(), endAt
        );

        if (slots.size() != requiredSlots) {
            throw new BusinessException(ErrorCode.SLOT_NOT_FOUND);
        }

        return slots;
    }

    private void validateStoreReferences(Store store, Staff staff, ServiceMenu menu, TimeSlot slot) {
        if (!staff.getStore().getId().equals(store.getId())) {
            throw new BusinessException(ErrorCode.STAFF_STORE_MISMATCH);
        }
        if (!menu.getStore().getId().equals(store.getId())) {
            throw new BusinessException(ErrorCode.MENU_STORE_MISMATCH);
        }
        if (!slot.getStore().getId().equals(store.getId())) {
            throw new BusinessException(ErrorCode.SLOT_STORE_MISMATCH);
        }
        if (!slot.getStaff().getId().equals(staff.getId())) {
            throw new BusinessException(ErrorCode.SLOT_STAFF_MISMATCH);
        }
    }
}