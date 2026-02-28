package com.salon.core.security;

import com.salon.core.domain.enums.UserRole;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class StoreAccessValidator {

    public void validateAccess(Long storeId) {
        AuthenticatedUser user = AuthenticatedUser.current();

        if (user.role() == UserRole.ADMIN) {
            return;
        }

        if (user.role() == UserRole.STORE_ADMIN) {
            if (!user.hasStoreAccess(storeId)) {
                throw new AccessDeniedException(
                        "Store access denied: user storeId=" + user.storeId() + ", requested storeId=" + storeId);
            }
        }
    }
}
