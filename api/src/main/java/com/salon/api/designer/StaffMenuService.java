package com.salon.api.designer;

import com.salon.api.designer.dto.StaffMenuResponse;
import com.salon.core.common.exception.BusinessException;
import com.salon.core.common.exception.ErrorCode;
import com.salon.core.domain.repository.StaffMenuRepository;
import com.salon.core.domain.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StaffMenuService {

    private final StaffRepository staffRepository;
    private final StaffMenuRepository staffMenuRepository;

    public List<StaffMenuResponse> getMenus(Long storeId, Long staffId) {
        staffRepository.findById(staffId)
                .filter(s -> s.getStore().getId().equals(storeId))
                .orElseThrow(() -> new BusinessException(ErrorCode.STAFF_NOT_FOUND));

        return staffMenuRepository.findByStaffId(staffId).stream()
                .map(StaffMenuResponse::from)
                .toList();
    }
}