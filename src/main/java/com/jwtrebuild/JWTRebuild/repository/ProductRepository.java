package com.jwtrebuild.JWTRebuild.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jwtrebuild.JWTRebuild.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

}
