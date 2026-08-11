package com.taskManagement.security;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.taskManagement.exception.ApiException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

	@Value("${jwtkey}")
	private String secretKey;

//	public static Key generateKey() {
//		Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//		String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
//		System.out.println("generated key :  " + encodedKey);
//		return key;
//	}

	public String generateToken(String username, String role) {
		Date currentDate = new Date();
		Date expireDate = new Date(currentDate.getTime() + 1000 * 60 * 60);// one hour expiry
		System.out.println(secretKey);
		String token = Jwts.builder().setSubject(username).claim("role", role).setIssuedAt(currentDate)
				.setExpiration(expireDate).signWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey)))
				.compact();
		return token;
	}

	public String getUsernameFromToken(String token) {
		Claims cliams = Jwts.parser().setSigningKey(Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey)))
				.parseClaimsJws(token).getBody();
		return cliams.getSubject();
	}

	public String getRoleFromToken(String token) {
		// TODO Auto-generated method stub
		return (String) Jwts.parser().setSigningKey(Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey)))
				.parseClaimsJws(token).getBody().get("role");
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey)))
					.parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			throw new ApiException("token issue :  " + e.getMessage());
		}

	}

}
