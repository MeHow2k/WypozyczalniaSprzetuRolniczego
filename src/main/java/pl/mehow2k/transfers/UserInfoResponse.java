package pl.mehow2k.transfers;

import java.util.List;

public class UserInfoResponse {

    private Long id;
    private String username;
    private List<String> roles;

    public UserInfoResponse(Long id, String username, List<String> roles) {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }
    public UserInfoResponse(String username, List<String> roles) { this.username = username; this.roles = roles; }
    public String getUsername() { return username; }
    public List<String> getRoles() { return roles; }
    public Long getId() { return id; }
}
