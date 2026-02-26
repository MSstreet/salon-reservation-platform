package com.salon.api.designer.dto;

import com.salon.domain.entity.Staff;
import com.salon.domain.enums.StaffRole;
import com.salon.domain.enums.StaffStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DesignerResponse {

    private final Long staffId;
    private final String name;
    private final StaffRole role;
    private final StaffStatus status;

    public static DesignerResponse from(Staff staff) {
        return new DesignerResponse(
                staff.getId(),
                staff.getName(),
                staff.getRole(),
                staff.getStatus()
        );
    }
}
