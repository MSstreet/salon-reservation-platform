package com.salon.core.security;

import com.salon.core.domain.enums.UserRole;
import org.springframework.security.core.context.SecurityContextHolder;

public record AuthenticatedUser(
        Long userId,
        UserRole role,
        Long storeId
) {

    public boolean hasStoreAccess(Long targetStoreId) {
        if (role == UserRole.ADMIN) {
            return true;
        }
        if (role == UserRole.STORE_ADMIN) {
            return targetStoreId != null && targetStoreId.equals(storeId);
        }
        return true;
    }

    public static AuthenticatedUser current() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            throw new IllegalStateException("No authenticated user found in SecurityContext");
        }
        return user;
    }
}
