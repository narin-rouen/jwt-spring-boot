package com.jwtrebuild.JWTRebuild.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jwtrebuild.JWTRebuild.dto.request.RefreshTokenRequest;
import com.jwtrebuild.JWTRebuild.dto.request.SignInRequest;
import com.jwtrebuild.JWTRebuild.dto.request.SignUpRequest;
import com.jwtrebuild.JWTRebuild.dto.response.AuthResponse;
import com.jwtrebuild.JWTRebuild.entity.User;
import com.jwtrebuild.JWTRebuild.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final JWTService jwtService;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;

	private static final String DEFAULT_USER_ROLE = "USER";

	@Transactional
	public AuthResponse signUp(SignUpRequest request) {
		// Check if user already exists
		if (userRepository.existsByUserEmail(request.getUserEmail())) {
			throw new RuntimeException("User with this email already exists");
		}

		// Determine role - use request value or default
		String userRole = (request.getUserRole() != null && !request.getUserRole().isBlank()) ? request.getUserRole()
				: DEFAULT_USER_ROLE;

		// Create new user with encoded password
		User user = User.builder().userEmail(request.getUserEmail())
				.userPassword(passwordEncoder.encode(request.getUserPassword())).userFullName(request.getUserFullName())
				.userRole(userRole) // Use determined role
				.build();

		User savedUser = userRepository.save(user);

		// Generate tokens
		String accessToken = jwtService.generateAccessToken(savedUser);
		String deviceId = "web-browser";
		String refreshToken = jwtService.generateRefreshToken(savedUser, deviceId);

		AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder().userId(savedUser.getUserId())
				.userFullName(savedUser.getUserFullName()).userEmail(savedUser.getUserEmail())
				.userRole(savedUser.getUserRole()).build();

		return AuthResponse.builder().token(accessToken).refreshToken(refreshToken).tokenType("Bearer")
				.expiredTime("15 mn") // Set your expiration time
				.user(userInfo).build();
	}

	public AuthResponse signIn(SignInRequest request) {
		// Authenticate user credentials
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getUserEmail(), request.getUserPassword()));

		// Find user details
		User user = userRepository.findByUserEmail(request.getUserEmail())
				.orElseThrow(() -> new RuntimeException("User not found"));

		// Generate tokens
		String accessToken = jwtService.generateAccessToken(user);
		String deviceId = "web-browser";
		String refreshToken = jwtService.generateRefreshToken(user, deviceId);

		AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder().userId(user.getUserId())
				.userFullName(user.getUserFullName()).userEmail(user.getUserEmail()).userRole(user.getUserRole())
				.build();

		return AuthResponse.builder().token(accessToken).refreshToken(refreshToken).tokenType("Bearer")
				.expiredTime("24h").user(userInfo).build();
	}

	public AuthResponse refreshToken(RefreshTokenRequest request) {
		String refreshToken = request.getRefreshToken();
		String userEmail = jwtService.extractUsername(refreshToken, true)
				.orElseThrow(() -> new RuntimeException("Invalid token"));

		// Find user
		User user = userRepository.findByUserEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));

		// Validate refresh token
		if (!jwtService.isTokenValid(refreshToken, user, true)) {
			throw new RuntimeException("Invalid refresh token");
		}

		// Generate new access token (keep same refresh token)
		String newAccessToken = jwtService.generateAccessToken(user);

		// Create UserInfo (not UserResponse)
		AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder().userId(user.getUserId())
				.userFullName(user.getUserFullName()).userEmail(user.getUserEmail()).userRole(user.getUserRole())
				.build();

		// Return response
		return AuthResponse.builder().token(newAccessToken) // Use "token" not "accessToken"
				.refreshToken(refreshToken).tokenType("Bearer").expiredTime("24h").user(userInfo).build();
	}

	public AuthResponse.UserInfo getCurrentUser() {
		// Extract authentication from SecurityContext
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new RuntimeException("No authenticated user found");
		}

		String userEmail = authentication.getName();

		User user = userRepository.findByUserEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));

		// Return UserInfo (nested class in AuthResponse)
		return AuthResponse.UserInfo.builder().userId(user.getUserId()).userFullName(user.getUserFullName())
				.userEmail(user.getUserEmail()).userRole(user.getUserRole()).build();
	}
}
