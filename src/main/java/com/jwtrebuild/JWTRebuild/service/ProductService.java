package com.jwtrebuild.JWTRebuild.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.jwtrebuild.JWTRebuild.dto.request.ProductRequest;
import com.jwtrebuild.JWTRebuild.dto.response.ProductListResponse;
import com.jwtrebuild.JWTRebuild.dto.response.ProductResponse;
import com.jwtrebuild.JWTRebuild.entity.Product;
import com.jwtrebuild.JWTRebuild.repository.ProductRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;

	public ProductResponse getAllProducts(int page, int size, String sortBy, String sortDir) {
		Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

		Pageable pageable = (Pageable) PageRequest.of(page, size, sort);
		Page<Product> productPage = productRepository.findAll(pageable);

		List<ProductResponse.ProductSummary> summaries = productPage.getContent().stream()
				.map(product -> ProductResponse.ProductSummary.builder().productId(product.getProductId())
						.productName(product.getProductName()).productPrice(product.getProductPrice())
						.productCost(product.getProductCost()).build())
				.collect(Collectors.toList());

		return ProductResponse.builder().product(summaries).currentPage(productPage.getNumber())
				.totalPages(productPage.getTotalPages()).totalItems(productPage.getTotalElements())
				.pageSize(productPage.getSize()).hasNext(productPage.hasNext()).hasPrevious(productPage.hasPrevious())
				.build();
	}

	@Transactional
	public ProductListResponse createProduct(ProductRequest request) {
		Product product = new Product();
		product.setProductName(request.getProductName());
		product.setProductDescription(request.getProductDescription());
		product.setProductPrice(request.getProductPrice());
		product.setProductCost(request.getProductCost());

		Product savedProduct = productRepository.save(product);

		return ProductListResponse.builder().productId(savedProduct.getProductId())
				.productName(savedProduct.getProductName()).productDescription(savedProduct.getProductDescription())
				.productPrice(savedProduct.getProductPrice()).productCost(savedProduct.getProductCost()).build();
	}

	public ProductListResponse getProductById(int id) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

		return ProductListResponse.builder().productId(product.getProductId()).productName(product.getProductName())
				.productDescription(product.getProductDescription()).productPrice(product.getProductPrice())
				.productCost(product.getProductCost()).build();

	}

	@Transactional
	public ProductListResponse updateProduct(int id, ProductRequest request) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

		product.setProductName(request.getProductName());
		product.setProductDescription(request.getProductDescription());
		product.setProductPrice(request.getProductPrice());
		product.setProductCost(request.getProductCost());

		Product updatedProduct = productRepository.save(product);

		return ProductListResponse.builder().productId(updatedProduct.getProductId())
				.productName(updatedProduct.getProductName()).productDescription(updatedProduct.getProductDescription())
				.productPrice(updatedProduct.getProductPrice()).productCost(updatedProduct.getProductCost()).build();
	}
}
