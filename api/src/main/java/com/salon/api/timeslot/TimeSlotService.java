package com.salon.api.timeslot;

import com.salon.api.timeslot.dto.TimeSlotResponse;
import com.salon.core.common.exception.BusinessException;
import com.salon.core.common.exception.ErrorCode;
import com.salon.core.domain.entity.ServiceMenu;
import com.salon.core.domain.entity.TimeSlot;
import com.salon.core.domain.enums.SlotStatus;
import com.salon.core.domain.repository.ServiceMenuRepository;
import com.salon.core.domain.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimeSlotService {

    private static final int SLOT_UNIT_MINUTES = 30;

    private final TimeSlotRepository timeSlotRepository;
    private final ServiceMenuRepository serviceMenuRepository;

    public List<TimeSlotResponse> getSlots(Long storeId, LocalDate date, Long staffId,
                                           SlotStatus status, Long menuId) {
        List<TimeSlot> slots = timeSlotRepository.findByStoreIdAndDateAndStaffId(storeId, date, staffId);

        if (menuId != null) {
            return filterByMenu(slots, storeId, menuId);
        }

        return slots.stream()
                .filter(slot -> status == null || slot.getStatus() == status)
                .map(TimeSlotResponse::from)
                .toList();
    }

    private List<TimeSlotResponse> filterByMenu(List<TimeSlot> slots, Long storeId, Long menuId) {
        ServiceMenu menu = serviceMenuRepository.findByIdAndStoreId(menuId, storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));

        int requiredSlots = (int) Math.ceil((double) menu.getDurationMin() / SLOT_UNIT_MINUTES);

        Set<LocalDateTime> openStartTimes = slots.stream()
                .filter(s -> s.getStatus() == SlotStatus.OPEN)
                .map(TimeSlot::getStartAt)
                .collect(Collectors.toSet());

        return slots.stream()
                .filter(slot -> slot.getStatus() == SlotStatus.OPEN)
                .filter(slot -> hasConsecutiveOpenSlots(slot.getStartAt(), requiredSlots, openStartTimes))
                .map(TimeSlotResponse::from)
                .toList();
    }

    private boolean hasConsecutiveOpenSlots(LocalDateTime startAt, int requiredSlots,
                                            Set<LocalDateTime> openStartTimes) {
        for (int i = 0; i < requiredSlots; i++) {
            if (!openStartTimes.contains(startAt.plusMinutes((long) i * SLOT_UNIT_MINUTES))) {
                return false;
            }
        }
        return true;
    }
}