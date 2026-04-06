package com.salon.admin.designer;

import com.salon.admin.designer.dto.DesignerCreateRequest;
import com.salon.admin.designer.dto.DesignerResponse;
import com.salon.core.common.exception.BusinessException;
import com.salon.core.common.exception.ErrorCode;
import com.salon.core.domain.entity.Staff;
import com.salon.core.domain.entity.Store;
import com.salon.core.domain.enums.StaffStatus;
import com.salon.core.domain.repository.StaffRepository;
import com.salon.core.domain.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DesignerAdminService {

    private final StoreRepository storeRepository;
    private final StaffRepository staffRepository;

    @Transactional(readOnly = true)
    public List<DesignerResponse> getAll(Long storeId) {
        return staffRepository.findByStoreId(storeId).stream()
                .map(DesignerResponse::from)
                .toList();
    }

    @Transactional
    public DesignerResponse create(Long storeId, DesignerCreateRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        Staff staff = Staff.create(store, request.getName(), request.getRole(), StaffStatus.ACTIVE);
        staffRepository.save(staff);

        return DesignerResponse.from(staff);
    }

    @Transactional
    public DesignerResponse updateStatus(Long storeId, Long staffId, StaffStatus status) {
        Staff staff = staffRepository.findById(staffId)
                .filter(s -> s.getStore().getId().equals(storeId))
                .orElseThrow(() -> new BusinessException(ErrorCode.STAFF_NOT_FOUND));

        staff.changeStatus(status);
        return DesignerResponse.from(staff);
    }
}