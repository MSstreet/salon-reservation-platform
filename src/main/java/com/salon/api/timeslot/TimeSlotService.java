package com.salon.api.timeslot;

import com.salon.api.timeslot.dto.TimeSlotResponse;
import com.salon.domain.enums.SlotStatus;
import com.salon.domain.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;

    public List<TimeSlotResponse> getSlots(Long storeId, LocalDate date, Long staffId, SlotStatus status) {
        return timeSlotRepository.findByStoreIdAndDateAndStaffId(storeId, date, staffId).stream()
                .filter(slot -> status == null || slot.getStatus() == status)
                .map(TimeSlotResponse::from)
                .toList();
    }
}
