package com.salon.admin.schedule;

import com.salon.admin.schedule.dto.StaffScheduleCreateRequest;
import com.salon.admin.schedule.dto.StaffScheduleResponse;
import com.salon.core.common.exception.BusinessException;
import com.salon.core.common.exception.ErrorCode;
import com.salon.core.domain.entity.Staff;
import com.salon.core.domain.entity.StaffSchedule;
import com.salon.core.domain.entity.Store;
import com.salon.core.domain.repository.StaffRepository;
import com.salon.core.domain.repository.StaffScheduleRepository;
import com.salon.core.domain.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffScheduleAdminService {

    private final StoreRepository storeRepository;
    private final StaffRepository staffRepository;
    private final StaffScheduleRepository staffScheduleRepository;

    @Transactional(readOnly = true)
    public List<StaffScheduleResponse> getSchedules(Long storeId, LocalDate date, Long staffId) {
        return staffScheduleRepository.search(storeId, date, staffId).stream()
                .map(StaffScheduleResponse::from)
                .toList();
    }

    @Transactional
    public StaffScheduleResponse create(Long storeId, StaffScheduleCreateRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        Staff staff = staffRepository.findById(request.getStaffId())
                .filter(s -> s.getStore().getId().equals(storeId))
                .orElseThrow(() -> new BusinessException(ErrorCode.STAFF_NOT_FOUND));

        if (staffScheduleRepository.findByStaffIdAndDate(staff.getId(), request.getDate()).isPresent()) {
            throw new BusinessException(ErrorCode.SCHEDULE_ALREADY_EXISTS);
        }

        StaffSchedule schedule = StaffSchedule.create(
                store, staff,
                request.getDate(),
                request.getStartTime(),
                request.getEndTime(),
                request.getType()
        );
        staffScheduleRepository.save(schedule);

        return StaffScheduleResponse.from(schedule);
    }
}