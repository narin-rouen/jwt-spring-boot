package com.jwtrebuild.JWTRebuild.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductRequest {

	@NotBlank(message = "Product name is reqiured!")
	@Size(min = 2, max = 100, message = "Product name must be between 2 to 100 charaters.")
	private String productName;

	@Size(max = 500, message = "Product description cannot exceed 500 charaters.")
	private String productDescription;

	@NotNull(message = "Product price is required")
	@Positive(message = "Price must be positive")
	@DecimalMin(value = "0.01", message = "Price must be at least 0.01")
	private double productPrice;

	@NotNull(message = "Product cost is required")
	@Positive(message = "Cost must be positive")
	@DecimalMin(value = "0.01", message = "Cost must be at least 0.01")
	private double productCost;
}
