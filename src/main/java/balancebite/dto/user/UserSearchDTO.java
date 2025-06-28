package balancebite.dto.user;

public class UserSearchDTO {
    private final Long id;
    private final String userName;

    public UserSearchDTO(Long id, String userName) {
        this.id = id;
        this.userName = userName;
    }

    public Long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }
}