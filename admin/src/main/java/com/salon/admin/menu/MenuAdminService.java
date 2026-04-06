package com.salon.admin.menu;

import com.salon.admin.menu.dto.MenuCreateRequest;
import com.salon.admin.menu.dto.MenuResponse;
import com.salon.core.common.exception.BusinessException;
import com.salon.core.common.exception.ErrorCode;
import com.salon.core.domain.entity.ServiceMenu;
import com.salon.core.domain.entity.Store;
import com.salon.core.domain.enums.MenuStatus;
import com.salon.core.domain.repository.ServiceMenuRepository;
import com.salon.core.domain.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuAdminService {

    private final StoreRepository storeRepository;
    private final ServiceMenuRepository serviceMenuRepository;

    @Transactional(readOnly = true)
    public List<MenuResponse> getAll(Long storeId) {
        return serviceMenuRepository.findAllByStoreId(storeId).stream()
                .map(MenuResponse::from)
                .toList();
    }

    @Transactional
    public MenuResponse create(Long storeId, MenuCreateRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        ServiceMenu menu = ServiceMenu.create(
                store, request.getName(), request.getDurationMin(),
                request.getPrice(), MenuStatus.ACTIVE
        );
        serviceMenuRepository.save(menu);

        return MenuResponse.from(menu);
    }

    @Transactional
    public MenuResponse toggleStatus(Long storeId, Long menuId) {
        ServiceMenu menu = serviceMenuRepository.findByIdAndStoreId(menuId, storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));

        menu.toggleStatus();
        return MenuResponse.from(menu);
    }
}