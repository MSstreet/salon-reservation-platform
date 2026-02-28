package com.salon.api.auth;

import com.salon.api.auth.dto.MockLoginRequest;
import com.salon.api.auth.dto.TokenResponse;
import com.salon.core.common.response.ApiResponse;
import com.salon.core.domain.enums.UserRole;
import com.salon.core.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/mock-login")
    public ResponseEntity<ApiResponse<TokenResponse>> mockLogin(@Valid @RequestBody MockLoginRequest request) {
        if (request.role() == UserRole.STORE_ADMIN && request.storeId() == null) {
            throw new IllegalArgumentException("storeId is required for STORE_ADMIN role");
        }

        String token = jwtTokenProvider.generateToken(request.userId(), request.role(), request.storeId());
        TokenResponse tokenResponse = TokenResponse.of(token, jwtTokenProvider.getExpirationMs());

        return ResponseEntity.ok(ApiResponse.success(tokenResponse));
    }
}
