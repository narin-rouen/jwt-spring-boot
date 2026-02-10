package com.jwtrebuild.JWTRebuild.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {

	@NotBlank(message = "Refresh token is reqiured!")
	private String refreshToken;
}
