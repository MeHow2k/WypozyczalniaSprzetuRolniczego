package pl.mehow2k.transfers;

import java.util.List;

public class UserInfoResponse {

    private String username;
    private List<String> roles;
    public UserInfoResponse(String username, List<String> roles) { this.username = username; this.roles = roles; }
    public String getUsername() { return username; }
    public List<String> getRoles() { return roles; }
}
