package com.salon.api.auth;

import com.salon.api.auth.dto.MockLoginRequest;
import com.salon.api.auth.dto.TokenResponse;
import com.salon.core.common.response.ApiResponse;
import com.salon.core.domain.enums.UserRole;
import com.salon.core.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 API (테스트용 Mock 로그인)")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "Mock 로그인", description = "테스트용 JWT 토큰 발급. STORE_ADMIN 역할은 storeId 필수.")
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
