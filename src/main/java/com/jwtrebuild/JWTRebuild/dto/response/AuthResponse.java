package com.jwtrebuild.JWTRebuild.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

	private String token;
	private String refreshToken;
	private String tokenType;
	private String expiredTime;
	private UserInfo user;

	@Data
	@Builder
	public static class UserInfo {
		private int userId;
		private String userFullName;
		private String userEmail;
		private String userRole;
	}
}
