package com.jwtrebuild.JWTRebuild.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponse {

	private List<ProductSummary> product;
	private int currentPage;
	private int totalPages;
	private long totalItems;
	private int pageSize;
	private boolean hasNext;
	private boolean hasPrevious;

	@Data
	@Builder
	public static class ProductSummary {
		private int productId;
		private String productName;
		private double productPrice;
		private double productCost;
	}

}
