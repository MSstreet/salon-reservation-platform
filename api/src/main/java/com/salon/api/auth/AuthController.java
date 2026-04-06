package com.salon.api.auth;

import com.salon.api.auth.dto.MockLoginRequest;
import com.salon.api.auth.dto.RefreshRequest;
import com.salon.api.auth.dto.TokenResponse;
import com.salon.core.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Mock 로그인", description = "테스트용 JWT 발급. STORE_ADMIN 역할은 storeId 필수.")
    @PostMapping("/mock-login")
    public ResponseEntity<ApiResponse<TokenResponse>> mockLogin(@Valid @RequestBody MockLoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.mockLogin(request)));
    }

    @Operation(summary = "토큰 갱신", description = "Refresh token으로 새 Access token과 Refresh token을 발급합니다. 기존 Refresh token은 즉시 폐기됩니다.")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.refresh(request.refreshToken())));
    }

    @Operation(summary = "로그아웃", description = "Refresh token을 폐기합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }
}