package com.jwtrebuild.JWTRebuild.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignInRequest {

	@NotBlank(message = "Email is reqiured!")
	@Email(message = "Email is not valid!")
	private String userEmail;

	@NotBlank(message = "Password is required!")
	private String userPassword;
}
