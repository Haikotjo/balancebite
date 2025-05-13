package balancebite.dto.user;

public class PublicUserDTO {
    private final Long id;
    private final String userName;

    public PublicUserDTO(Long id, String userName) {
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
