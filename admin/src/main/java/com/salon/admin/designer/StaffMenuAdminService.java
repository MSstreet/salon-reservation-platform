package com.salon.admin.designer;

import com.salon.admin.designer.dto.StaffMenuResponse;
import com.salon.core.common.exception.BusinessException;
import com.salon.core.common.exception.ErrorCode;
import com.salon.core.domain.entity.ServiceMenu;
import com.salon.core.domain.entity.Staff;
import com.salon.core.domain.entity.StaffMenu;
import com.salon.core.domain.repository.ServiceMenuRepository;
import com.salon.core.domain.repository.StaffMenuRepository;
import com.salon.core.domain.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffMenuAdminService {

    private final StaffRepository staffRepository;
    private final ServiceMenuRepository serviceMenuRepository;
    private final StaffMenuRepository staffMenuRepository;

    @Transactional(readOnly = true)
    public List<StaffMenuResponse> getMenus(Long storeId, Long staffId) {
        validateStaff(storeId, staffId);
        return staffMenuRepository.findByStaffId(staffId).stream()
                .map(StaffMenuResponse::from)
                .toList();
    }

    @Transactional
    public StaffMenuResponse addMenu(Long storeId, Long staffId, Long menuId) {
        Staff staff = validateStaff(storeId, staffId);

        ServiceMenu menu = serviceMenuRepository.findByIdAndStoreId(menuId, storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));

        if (staffMenuRepository.existsByStaffIdAndMenuId(staffId, menuId)) {
            throw new BusinessException(ErrorCode.STAFF_MENU_ALREADY_EXISTS);
        }

        StaffMenu staffMenu = staffMenuRepository.save(StaffMenu.create(staff, menu));
        return StaffMenuResponse.from(staffMenu);
    }

    @Transactional
    public void removeMenu(Long storeId, Long staffId, Long menuId) {
        validateStaff(storeId, staffId);

        StaffMenu staffMenu = staffMenuRepository.findByStaffIdAndMenuId(staffId, menuId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STAFF_MENU_NOT_FOUND));

        staffMenuRepository.delete(staffMenu);
    }

    private Staff validateStaff(Long storeId, Long staffId) {
        return staffRepository.findById(staffId)
                .filter(s -> s.getStore().getId().equals(storeId))
                .orElseThrow(() -> new BusinessException(ErrorCode.STAFF_NOT_FOUND));
    }
}