package com.jwtrebuild.JWTRebuild.config;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jwtrebuild.JWTRebuild.service.CustomUserDetailService;
import com.jwtrebuild.JWTRebuild.service.JWTService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final String BEARER_PREFIX = "Bearer ";
	private static final String USERNAME_ATTRIBUTE = "username";
	private static final String TOKEN_ATTRIBUTE = "jwt_token";

	private final JWTService jwtService;
	private final CustomUserDetailService customUserDetailService;

	@Override
	protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
			@NotNull FilterChain filterChain) throws ServletException, IOException {
		// Check if this is an auth endpoint - skip JWT processing
		String path = request.getServletPath();
		if (path.startsWith("/api/auth/signin") || path.startsWith("/api/auth/signup")
				|| path.startsWith("/api/auth/refresh") || path.equals("/api/auth/health")) {
			filterChain.doFilter(request, response);
			return;
		}

		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		// Skip JWT processes if no authorization header
		if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
			// For non-auth endpoints, return 401 if no token
			if (!path.startsWith("/api/public/")) {
				handleJwtException(response, "Missing or invalid Authorization header",
						HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
			filterChain.doFilter(request, response);
			return;
		}

		try {
			final String jwtToken = extractJwtFromHeader(authHeader);
			final String username = jwtService.extractUsername(jwtToken, false).orElse(null);

			// Add token and username to request for downstream use
			request.setAttribute(TOKEN_ATTRIBUTE, jwtToken);
			request.setAttribute(USERNAME_ATTRIBUTE, username);

			// skip authentication if username is null
			if (username == null) {
				filterChain.doFilter(request, response);
				return;
			}

			authenticatedUser(request, jwtToken, username);

		} catch (ExpiredJwtException e) {
			log.warn("JWT token expired for request: {}", request.getRequestURI());
			handleJwtException(response, "Token has expired", HttpServletResponse.SC_UNAUTHORIZED);
			return;
		} catch (SignatureException e) {
			log.warn("Invalid JWT signature for request: {}", request.getRequestURI());
			handleJwtException(response, "Invalid token signature", HttpServletResponse.SC_UNAUTHORIZED);
			return;
		} catch (MalformedJwtException e) {
			log.warn("Malformed JWT token for request: {}", request.getRequestURI());
			handleJwtException(response, "Malformed token", HttpServletResponse.SC_UNAUTHORIZED);
			return;
		} catch (UsernameNotFoundException e) {
			log.warn("User not found for JWT token: {}", e.getMessage());
			handleJwtException(response, "User not found", HttpServletResponse.SC_UNAUTHORIZED);
			return;
		} catch (Exception e) {
			log.error("Unexpected error during JWT authentication", e);
			handleJwtException(response, "Authentication failed", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		filterChain.doFilter(request, response);
	}

	private void authenticatedUser(HttpServletRequest request, String jwtToken, String username) {
		UserDetails userDetails = customUserDetailService.loadUserByUsername(username);

		if (jwtService.isTokenValid(jwtToken, userDetails, false)) {
			SecurityContext context = SecurityContextHolder.createEmptyContext();
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
					userDetails.getAuthorities());

			authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			context.setAuthentication(authToken);
			SecurityContextHolder.setContext(context);

			log.debug("Successfully authenticated user: {}", username);
		} else {
			log.warn("Invalid JWT token for user: {}", username);
		}

	}

	private String extractJwtFromHeader(String authHeader) {
		if (authHeader.length() <= BEARER_PREFIX.length()) {
			throw new MalformedJwtException("Authorization header is too short");
		}
		return authHeader.substring(BEARER_PREFIX.length());
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String path = request.getServletPath();
		// Return true to skip filtering for these paths
		return path.startsWith("/api/auth/signin") || path.startsWith("/api/auth/signup")
				|| path.startsWith("/api/auth/refresh") || path.startsWith("/swagger")
				|| path.startsWith("/v3/api-docs") || path.startsWith("/actuator/health")
				|| path.equals("/api/auth/health");
	}

	private void handleJwtException(HttpServletResponse response, String message, int statusCode) throws IOException {
		response.setStatus(statusCode);
		response.setContentType("application/json");
		response.getWriter().write(String.format("{\"error\": \"Unauthorized\", \"message\": \"%s\"}", message));
	}

}
