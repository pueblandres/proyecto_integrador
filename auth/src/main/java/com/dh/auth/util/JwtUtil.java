package com.dh.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static io.jsonwebtoken.Jwts.*;


@Service
public class JwtUtil {

    private String SECRET_KEY = "secret"; // Cambiar por un valor seguro en producción
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // Token válido por 10 horas
    private Map<String, Boolean> revokedTokens = new HashMap<>();


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        if (claims == null) {
            throw new IllegalArgumentException("No se pudieron extraer las Claims del token.");
        }
        return claimsResolver.apply(claims);
    }


    private Claims extractAllClaims(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("El token es nulo o vacío.");
        }

        try {
            return parser()
                    .setSigningKey(SECRET_KEY.getBytes()) // Convierte la clave a bytes
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            System.err.println("Error al analizar el token: " + e.getMessage());
            return null;
        }
    }


    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())  // Convertir SECRET_KEY a byte[]
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token) && !isTokenRevoked(token));
    }


    public void revokeToken(String token) {
        revokedTokens.put(token, true);
    }


    public boolean isTokenRevoked(String token) {
        return revokedTokens.getOrDefault(token, false);
    }
}
