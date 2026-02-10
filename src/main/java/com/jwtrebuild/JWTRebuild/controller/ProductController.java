package com.jwtrebuild.JWTRebuild.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jwtrebuild.JWTRebuild.dto.request.ProductRequest;
import com.jwtrebuild.JWTRebuild.dto.response.ProductListResponse;
import com.jwtrebuild.JWTRebuild.dto.response.ProductResponse;
import com.jwtrebuild.JWTRebuild.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	// admin endpoints
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/api/admin/products")
	public ResponseEntity<ProductResponse> getAllProudctForAdmin(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "productId") String sortBy,
			@RequestParam(defaultValue = "asc") String sortDir) {
		ProductResponse response = productService.getAllProducts(page, size, sortBy, sortDir);
		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/api/admin/products")
	public ResponseEntity<ProductListResponse> createProduct(@Valid @RequestBody ProductRequest request) {
		ProductListResponse response = productService.createProduct(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/api/admin/products/{id}")
	public ResponseEntity<ProductListResponse> getProductByIdForAdmin(@PathVariable int id) {
		ProductListResponse response = productService.getProductById(id);
		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hashasAuthority('ADMIN')")
	@PostMapping("/api/admin/products/{id}")
	public ResponseEntity<ProductListResponse> updateProduct(@PathVariable int id,
			@Valid @RequestBody ProductRequest request) {
		ProductListResponse response = productService.updateProduct(id, request);
		return ResponseEntity.ok(response);
	}

	// users endpoints
	@PreAuthorize("hasAuthority('USER')")
	@GetMapping("/api/user/products")
	public ResponseEntity<ProductResponse> getAllProudctForUser(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "productId") String sortBy,
			@RequestParam(defaultValue = "asc") String sortDir) {
		ProductResponse response = productService.getAllProducts(page, size, sortBy, sortDir);
		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasAuthority('USER')")
	@GetMapping("/api/user/products/{id}")
	public ResponseEntity<ProductListResponse> getProductByIdForUser(@PathVariable int id) {
		ProductListResponse response = productService.getProductById(id);
		return ResponseEntity.ok(response);
	}

	// Share endpoints (ADMIN or USER)
	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	@GetMapping("/api/share/products")
	public ResponseEntity<ProductResponse> getAllProductsForShare(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "productId") String sortBy,
			@RequestParam(defaultValue = "asc") String sortDir) {
		ProductResponse response = productService.getAllProducts(page, size, sortBy, sortDir);
		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	@GetMapping("/api/share/products/{id}")
	public ResponseEntity<ProductListResponse> getProductByIdForShare(@PathVariable int id) {
		ProductListResponse response = productService.getProductById(id);
		return ResponseEntity.ok(response);
	}
}
