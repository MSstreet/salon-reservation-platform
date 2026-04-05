package com.salon.api.auth;

import com.salon.api.auth.dto.MockLoginRequest;
import com.salon.api.auth.dto.TokenResponse;
import com.salon.core.common.exception.BusinessException;
import com.salon.core.common.exception.ErrorCode;
import com.salon.core.domain.enums.UserRole;
import com.salon.core.infrastructure.auth.RefreshTokenService;
import com.salon.core.infrastructure.auth.RefreshTokenService.RefreshTokenClaims;
import com.salon.core.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    public TokenResponse mockLogin(MockLoginRequest request) {
        if (request.role() == UserRole.STORE_ADMIN && request.storeId() == null) {
            throw new IllegalArgumentException("storeId is required for STORE_ADMIN role");
        }

        String accessToken = jwtTokenProvider.generateToken(request.userId(), request.role(), request.storeId());
        String refreshToken = refreshTokenService.issue(request.userId(), request.role(), request.storeId());

        return TokenResponse.of(accessToken, refreshToken, jwtTokenProvider.getExpirationMs());
    }

    public TokenResponse refresh(String refreshToken) {
        RefreshTokenClaims claims = refreshTokenService.validate(refreshToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.REFRESH_TOKEN_INVALID));

        refreshTokenService.revoke(refreshToken);

        String newAccessToken = jwtTokenProvider.generateToken(claims.userId(), claims.role(), claims.storeId());
        String newRefreshToken = refreshTokenService.issue(claims.userId(), claims.role(), claims.storeId());

        return TokenResponse.of(newAccessToken, newRefreshToken, jwtTokenProvider.getExpirationMs());
    }

    public void logout(String refreshToken) {
        refreshTokenService.revoke(refreshToken);
    }
}