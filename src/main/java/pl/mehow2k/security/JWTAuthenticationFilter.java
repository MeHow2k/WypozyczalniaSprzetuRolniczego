package pl.mehow2k.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.mehow2k.repositories.UserRepository;
import pl.mehow2k.models.User;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JWTTokenProvider tokenProvider;

    @Autowired // POPRAWKA 1: Brakowało adnotacji, przez co UserRepository byłoby nullem (NullPointerException)
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromJWT(jwt);

                //Pobieramy użytkownika z bazy danych i mapujemy jego obiekty Role na SimpleGrantedAuthority
                Optional<User> userOpt = userRepository.findByUsername(username);

                if (userOpt.isPresent()) {
                    List<SimpleGrantedAuthority> authorities = userOpt.get().getRoles().stream()
                            .map(role -> new SimpleGrantedAuthority(role.getName())) // Pobiera np. "ROLE_USER"
                            .toList();

                    // Bezpiecznie autoryzujemy użytkownika w kontekście Spring Security
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            // W logach produkcyjnych warto odnotować błąd walidacji tokenu
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}