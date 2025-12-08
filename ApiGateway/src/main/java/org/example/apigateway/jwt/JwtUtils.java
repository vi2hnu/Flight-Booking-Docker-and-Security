package org.example.apigateway.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;


@Component
public class JwtUtils {

    @Value("${jwtSecret}")
    private String jwtSecret;

    public void validateToken(final String token) {
        Jwts.parserBuilder()
            .setSigningKey(key())
            .build()
            .parseClaimsJws(token);
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}
