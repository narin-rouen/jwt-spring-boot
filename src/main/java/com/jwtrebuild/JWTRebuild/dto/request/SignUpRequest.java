package com.jwtrebuild.JWTRebuild.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequest {

	@NotBlank(message = "Full name is reqiured!")
	@Size(min = 2, max = 100, message = "Full name must be between 2 to 100 charaters.")
	private String userFullName;

	@NotBlank(message = "Email is reqiured!")
	@Email(message = "Invalid email format")
	private String userEmail;

	@NotBlank(message = "Password is reqiured!")
	@Size(min = 6, message = "Password must be at least 6 charaters.")
	private String userPassword;

	private String userRole;
}
