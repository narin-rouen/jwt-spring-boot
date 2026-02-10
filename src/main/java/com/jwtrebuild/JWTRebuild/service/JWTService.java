package com.jwtrebuild.JWTRebuild.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JWTService {

	private final SecretKey accessTokenKey;
	private final SecretKey refreshTokenKey;
	private final String issuer;

	@Value("${jwt.access-token.expiration:900000}") // 15 minutes
	private long accessTokenExpiration;

	@Value("${jwt.refresh-token.expiration:604800000}") // 7 days
	private long refreshTokenExpiration;

	public JWTService(@Value("${jwt.access-token.secret}") String accessTokenSecret,
			@Value("${jwt.refresh-token.secret}") String refreshTokenSecret,
			@Value("${jwt.issuer:narin-company}") String issuer) {
		this.accessTokenKey = Keys.hmacShaKeyFor(accessTokenSecret.getBytes(StandardCharsets.UTF_8));
		this.refreshTokenKey = Keys.hmacShaKeyFor(refreshTokenSecret.getBytes(StandardCharsets.UTF_8));
		this.issuer = issuer;

		validateKeyLength(accessTokenSecret, "Access token");
		validateKeyLength(refreshTokenSecret, "Refresh token");
	}

	private void validateKeyLength(String secret, String tokeType) {
		byte[] keyByte = secret.getBytes(StandardCharsets.UTF_8);
		if (keyByte.length < 32) {
			throw new IllegalArgumentException(String.format("Secret keys must be at least 32 charaters", tokeType));
		}
	}

	public String generateAccessToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("authorities", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
		claims.put("token_type", "access");
		return buildToken(claims, userDetails.getUsername(), accessTokenExpiration, accessTokenKey);
	}

	public String generateRefreshToken(UserDetails userDetails, String deviceId) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("token_type", "refresh");
		claims.put("device_id", deviceId);
		return buildToken(claims, userDetails.getUsername(), refreshTokenExpiration, refreshTokenKey);
	}

	private String buildToken(Map<String, Object> claims, String subject, long expiration, SecretKey signingKey) {
		return Jwts.builder().claims(claims).subject(subject).issuer(issuer)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + expiration)).signWith(signingKey, Jwts.SIG.HS256)
				.compact();
	}

	public Optional<String> extractUsername(String token, boolean isRefreshToken) {
		return extractClaim(token, Claims::getSubject, isRefreshToken);
	}

	public <T> Optional<T> extractClaim(String token, Function<Claims, T> resolver, boolean isRefreshToken) {
		try {
			Claims claims = extractAllClaims(token, isRefreshToken);
			return Optional.ofNullable(resolver.apply(claims));
		} catch (Exception e) {
			log.warn("Failed to extract claim from token: {}", e.getMessage());
			return Optional.empty();
		}
	}

	public boolean isTokenValid(String token, UserDetails userDetails, boolean isRefreshToken) {
		try {
			final String username = extractUsername(token, isRefreshToken).orElse(null);
			if (username == null)
				return false;

			Claims claims = extractAllClaims(token, isRefreshToken);

			// validate token type
			String tokeType = claims.get("token_type", String.class);
			if (isRefreshToken && !"refresh".equals(tokeType))
				return false;
			if (!isRefreshToken && !"access".equals(tokeType))
				return false;

			// validate issuer
			if (!issuer.equals(claims.getIssuer()))
				return false;

			return username.equals(userDetails.getUsername()) && !isTokenExpired(token, isRefreshToken);
		} catch (Exception e) {
			log.warn("Token validation failed: {}", e.getMessage());
			return false;
		}
	}

	private boolean isTokenExpired(String token, boolean isRefreshToken) {
		return extractClaim(token, Claims::getExpiration, isRefreshToken)
				.map(expiration -> expiration.before(new Date())).orElse(true);
	}

	private Claims extractAllClaims(String token, boolean isRefreshToken) {
		JwtParser parser = Jwts.parser().verifyWith(isRefreshToken ? refreshTokenKey : accessTokenKey)
				.requireIssuer(issuer).build();
		return parser.parseSignedClaims(token).getPayload();
	}

	public boolean validateTokenStructure(String token) {
		try {
			String[] parts = token.split("\\.");
			if (parts.length != 3)
				return false;

			// Decode header to check algorithm
			String header = new String(Base64.getUrlDecoder().decode(parts[0]));
			return header.contains("HS256");
		} catch (Exception e) {
			return false;
		}
	}
}
