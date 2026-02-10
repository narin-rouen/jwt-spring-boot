package com.jwtrebuild.JWTRebuild.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jwtrebuild.JWTRebuild.dto.request.RefreshTokenRequest;
import com.jwtrebuild.JWTRebuild.dto.request.SignInRequest;
import com.jwtrebuild.JWTRebuild.dto.request.SignUpRequest;
import com.jwtrebuild.JWTRebuild.dto.response.AuthResponse;
import com.jwtrebuild.JWTRebuild.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/signup")
	public ResponseEntity<AuthResponse> signUp(@Valid @RequestBody SignUpRequest request) {
		AuthResponse response = authService.signUp(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/signin")
	public ResponseEntity<AuthResponse> signIn(@Valid @RequestBody SignInRequest request) {
		AuthResponse response = authService.signIn(request);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/refresh")
	public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
		AuthResponse response = authService.refreshToken(request);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/me")
	public ResponseEntity<AuthResponse.UserInfo> getCurrentUser() {
		AuthResponse.UserInfo userInfo = authService.getCurrentUser();
		return ResponseEntity.ok(userInfo);
	}

	@GetMapping("/health")
	public ResponseEntity<String> healthCheck() {
		return ResponseEntity.ok("Auth service is running!");
	}
}
