package com.sistradoc.apigateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // IMPORTANT: This MUST be the same secret key as in the user-service
    // TODO: Externalize this secret key and share it securely between services
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public boolean isTokenValid(String token) {
        // In a real scenario, you might also check against a user service
        // or a token blacklist, but for now, we just check the expiration.
        return !isTokenExpired(token);
    }
}
