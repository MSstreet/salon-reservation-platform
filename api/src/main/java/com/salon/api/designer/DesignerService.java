package com.salon.api.designer;

import com.salon.api.designer.dto.DesignerResponse;
import com.salon.core.common.exception.BusinessException;
import com.salon.core.common.exception.ErrorCode;
import com.salon.core.domain.enums.StaffRole;
import com.salon.core.domain.enums.StaffStatus;
import com.salon.core.domain.repository.StaffRepository;
import com.salon.core.domain.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DesignerService {

    private final StoreRepository storeRepository;
    private final StaffRepository staffRepository;

    public List<DesignerResponse> getDesigners(Long storeId) {
        storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        return staffRepository.findByStoreIdAndRoleAndStatus(storeId, StaffRole.DESIGNER, StaffStatus.ACTIVE)
                .stream()
                .map(DesignerResponse::from)
                .toList();
    }
}
