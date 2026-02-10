package com.jwtrebuild.JWTRebuild.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jwtrebuild.JWTRebuild.repository.UserRepository;

@Service
public class CustomUserDetailService implements UserDetailsService {

	private final UserRepository userRepository;

	// constructor to allow injection the repository
	public CustomUserDetailService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		com.jwtrebuild.JWTRebuild.entity.User user = userRepository.findByUserEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

		// Convert User entity to Spring Security's UserDetails
		return org.springframework.security.core.userdetails.User.withUsername(user.getUserEmail())
				.password(user.getUserPassword()).authorities(user.getUserRole()).accountExpired(false)
				.accountLocked(false).credentialsExpired(false).disabled(false).build();
	}

}
