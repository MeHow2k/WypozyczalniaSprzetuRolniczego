package pl.mehow2k.transfers;

import java.util.Set;

public class RegisterRequest {
    private String username; // Adres e-mail
    private String password;
    private Set<String> roles; // Np. ["ROLE_USER"]

    // Gettery i Settery
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}