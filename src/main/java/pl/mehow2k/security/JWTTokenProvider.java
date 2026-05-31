package pl.mehow2k.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;


@Component
public class JWTTokenProvider {

    // Spring wstrzykuje to pole zaraz po utworzeniu obiektu
    @Value("${jwt.secret}")
    private String jwtSecret;

    private final long JWT_EXPIRATION_MS = 86400000; // 24 godziny ważności tokenu

    // pobranie secreta dla tokenu z app properites -> zmiennej środowiskowej
    private SecretKey getSigningKey() {
        if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
            throw new IllegalStateException("Błąd: Klucz jwt.secret nie został załadowany z application.properties!");
        }
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // Generowanie tokenu po udanym logowaniu
    public String generateToken(String username, List<String> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_MS);

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey()) // Metoda wyciągnie już prawidłowy klucz
                .compact();
    }

    // Wyciąganie loginu użytkownika z tokenu
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    // Walidacja czy token jest poprawny i nie wygasł
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception ex) {
            System.out.println("JWT Validation Error: " + ex.getMessage());
            return false;
        }
    }
}