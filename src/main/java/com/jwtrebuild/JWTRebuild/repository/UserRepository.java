package com.jwtrebuild.JWTRebuild.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jwtrebuild.JWTRebuild.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	Optional<User> findByUserEmail(String userEmail);

	boolean existsByUserEmail(String userEmail);
}
