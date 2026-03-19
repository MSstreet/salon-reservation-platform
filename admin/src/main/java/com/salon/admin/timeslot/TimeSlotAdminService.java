package com.salon.admin.timeslot;

import com.salon.admin.timeslot.dto.TimeSlotGenerateRequest;
import com.salon.admin.timeslot.dto.TimeSlotGenerateResponse;
import com.salon.core.common.exception.BusinessException;
import com.salon.core.common.exception.ErrorCode;
import com.salon.core.domain.entity.Staff;
import com.salon.core.domain.entity.StaffSchedule;
import com.salon.core.domain.entity.Store;
import com.salon.core.domain.entity.TimeSlot;
import com.salon.core.domain.enums.ScheduleType;
import com.salon.core.domain.enums.SlotStatus;
import com.salon.core.domain.repository.StaffRepository;
import com.salon.core.domain.repository.StaffScheduleRepository;
import com.salon.core.domain.repository.StoreRepository;
import com.salon.core.domain.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeSlotAdminService {

    private static final int SLOT_DURATION_MIN = 30;

    private final StoreRepository storeRepository;
    private final StaffRepository staffRepository;
    private final StaffScheduleRepository staffScheduleRepository;
    private final TimeSlotRepository timeSlotRepository;

    @Transactional
    public TimeSlotGenerateResponse generate(Long storeId, TimeSlotGenerateRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        Staff staff = staffRepository.findById(request.getStaffId())
                .filter(s -> s.getStore().getId().equals(storeId))
                .orElseThrow(() -> new BusinessException(ErrorCode.STAFF_NOT_FOUND));

        List<StaffSchedule> schedules = staffScheduleRepository
                .findByStaffIdAndDateRange(staff.getId(), request.getStartDate(), request.getEndDate())
                .stream()
                .filter(s -> s.getType() == ScheduleType.WORK)
                .toList();

        int generatedCount = 0;
        int skippedCount = 0;

        for (StaffSchedule schedule : schedules) {
            Set<LocalDateTime> existingStartTimes = timeSlotRepository
                    .findByStoreIdAndDateAndStaffId(storeId, schedule.getDate(), staff.getId())
                    .stream()
                    .map(TimeSlot::getStartAt)
                    .collect(Collectors.toSet());

            List<TimeSlot> newSlots = buildSlots(store, staff, schedule, existingStartTimes);
            timeSlotRepository.saveAll(newSlots);

            generatedCount += newSlots.size();
            skippedCount += countExpectedSlots(schedule) - newSlots.size();
        }

        return TimeSlotGenerateResponse.of(generatedCount, skippedCount);
    }

    private List<TimeSlot> buildSlots(Store store, Staff staff, StaffSchedule schedule,
                                      Set<LocalDateTime> existingStartTimes) {
        List<TimeSlot> slots = new ArrayList<>();
        LocalDate date = schedule.getDate();
        LocalTime cursor = schedule.getStartTime();

        while (cursor.plusMinutes(SLOT_DURATION_MIN).compareTo(schedule.getEndTime()) <= 0) {
            LocalDateTime startAt = LocalDateTime.of(date, cursor);
            LocalDateTime endAt = startAt.plusMinutes(SLOT_DURATION_MIN);

            if (!existingStartTimes.contains(startAt)) {
                slots.add(TimeSlot.create(store, staff, date, startAt, endAt, SlotStatus.OPEN));
            }

            cursor = cursor.plusMinutes(SLOT_DURATION_MIN);
        }

        return slots;
    }

    private int countExpectedSlots(StaffSchedule schedule) {
        long totalMinutes = schedule.getStartTime().until(schedule.getEndTime(),
                java.time.temporal.ChronoUnit.MINUTES);
        return (int) (totalMinutes / SLOT_DURATION_MIN);
    }
}