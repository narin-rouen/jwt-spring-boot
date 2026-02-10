package com.jwtrebuild.JWTRebuild.dto.common;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

	private int statusCode;
	private String message;
	private T data;
	private String error;

	@Builder.Default
	private LocalDateTime timestamp = LocalDateTime.now();

	// Success helpers
	public static <T> ApiResponse<T> success(T data) {
		return ApiResponse.<T>builder().statusCode(200).message("Success").data(data).build();
	}

	public static <T> ApiResponse<T> success(T data, String message) {
		return ApiResponse.<T>builder().statusCode(200).message(message).data(data).build();
	}

	public static <T> ApiResponse<T> created(T data) {
		return ApiResponse.<T>builder().statusCode(201).message("Created successfully").data(data).build();
	}

	public static <T> ApiResponse<T> created(T data, String message) {
		return ApiResponse.<T>builder().statusCode(201).message(message).data(data).build();
	}

	// Error helpers
	public static ApiResponse<?> error(int statusCode, String error, String message) {
		return ApiResponse.builder().statusCode(statusCode).error(error).message(message).build();
	}

	public static ApiResponse<?> badRequest(String message) {
		return error(400, "Bad Request", message);
	}

	public static ApiResponse<?> notFound(String message) {
		return error(404, "Not Found", message);
	}

	public static ApiResponse<?> unauthorized(String message) {
		return error(401, "Unauthorized", message);
	}

	public static ApiResponse<?> forbidden(String message) {
		return error(403, "Forbidden", message);
	}

	public static ApiResponse<?> internalError(String message) {
		return error(500, "Internal Server Error", message);
	}
}
