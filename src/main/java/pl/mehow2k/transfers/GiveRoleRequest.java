package pl.mehow2k.transfers;

public class GiveRoleRequest {
    private String roleName; // Np. "ROLE_STAFF" lub "ROLE_ADMIN"

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
}
