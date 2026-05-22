package pl.mehow2k.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;

@Component
public class JWTTokenProvider {

    // sekret - w produkcji musi być ukryty!
    private final String JWT_SECRET = "twoj_bardzo_dlugi_i_super_tajny_klucz_do_podpisywania_tokenow_jwt_123456";
    private final long JWT_EXPIRATION_MS = 86400000; // 24 godziny ważności tokenu

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
    }

    // Generowanie tokenu po udanym logowaniu
    public String generateToken(String username, List<String> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_MS);

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles) // Wstrzykujemy role użytkownika do wnętrza tokenu
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    // Wyciąganie login użytkownika z tokenu
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
            // W razie błędu (zmanipulowany token, wygasły) zwracamy false
            return false;
        }
    }
}