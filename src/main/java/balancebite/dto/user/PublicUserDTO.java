package balancebite.dto.user;

import balancebite.model.user.Role;

import java.util.Set;

public class PublicUserDTO {
    private final Long id;
    private final String userName;
    private final Set<Role> roles;

    public PublicUserDTO(Long id, String userName, Set<Role> roles) {
        this.id = id;
        this.userName = userName;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public Set<Role> getRoles() {
        return roles;
    }
}
