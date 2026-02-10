package com.jwtrebuild.JWTRebuild.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductListResponse {

	private int productId;
	private String productName;
	private String productDescription;
	private double productPrice;
	private double productCost;

	@Data
	@Builder
	public static class CreatedBy {
		private int userId;
		private String userFullName;
		private String userEmail;
	}
}
