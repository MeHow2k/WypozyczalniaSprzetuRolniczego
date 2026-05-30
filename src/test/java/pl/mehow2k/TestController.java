package pl.mehow2k;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.HttpHeaders;
import pl.mehow2k.models.User;
import pl.mehow2k.repositories.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest(classes = pl.mehow2k.StartApplication.class)
@AutoConfigureMockMvc
public class TestController {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserRepository userRepository;

    //brak autoryzacji dla niezalogowanego użytkownika
    @Test
    public void shouldReturnForbiddenOrUnauthorizedWhenUserNotLoggedIn() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                // Oczekujemy, że system odrzuci żądanie bez ciasteczka/tokenu
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldLoginSuccessfullyAndReturnJwtCookie() throws Exception {
        //Przygotowujemy dane testowe
        String username = "test@rolnik.pl";
        String rawPassword = "PrawidloweHaslo123";
        String hashedPassword = new BCryptPasswordEncoder().encode(rawPassword);

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername(username);
        mockUser.setPassword(hashedPassword);
        mockUser.setRoles(Collections.emptySet());

        // Symulujemy, że Spring znajdzie tego rolnika w bazie danych
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        // Przygotowujemy paczkę JSON, którą frontend wysyła podczas logowania
        Map<String, String> loginRequest = Map.of(
                "username", username,
                "password", rawPassword
        );

        // Wysyłamy żądanie
        mockMvc.perform(post("/api/auth/login")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(loginRequest)))
                // Oczekujemy, że logowanie zakończy się sukcesem (HTTP 200)
                .andExpect(status().isOk())

                // Sprawdzamy nagłówek Set-Cookie: czy zawiera nazwę ciasteczka i flagę HttpOnly
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("HttpOnly")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("jwtToken")));
    }


    // rbac blokuje zwyklego użytkownika chcącego wysłać żądanie admina
    @Test
    @WithMockUser(username = "rolnik@test.pl", roles = {"CLIENT"}) //udaje ze zalogowany CLIENT
    public void shouldReturnForbiddenWhenUserIsOnlyClient() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                // Zwykły user nie ma wstępu do /admin, sprawdzamy czy dostanie 403
                .andExpect(status().isForbidden());
    }

    //sprawdzamy czy jako admin mozemy uzyskać liste userów
    @Test
    @WithMockUser(username = "admin@wypozyczalnia.pl", roles = {"ADMIN"}) // udaje zalogowanego ADMIN
    public void shouldReturnUsersListWhenUserIsAdmin() throws Exception {
        // tworzenie sztucznego użytkownika, którego nasze repozytorium ma zwrócić
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("janusz@gmail.com");
        mockUser.setRoles(Collections.emptySet());

        // mockito, gdy serwer poprosi o zwrocenie userRepository zwraca listę z sztucznym userem
        when(userRepository.findAll()).thenReturn(List.of(mockUser));

        mockMvc.perform(get("/api/admin/users")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk())
                //  sprawdzamy czy JSON ma strukturę i czy nie ma tam hasła
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("janusz@gmail.com"))
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }


}
