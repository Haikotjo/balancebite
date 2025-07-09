package balancebite.dto.user;

public class PendingClientDTO {

    private final Long id;
    private final String email;
    private final Long dietitianId;
    private final String dietitianName;

    public PendingClientDTO(Long id, String email, Long dietitianId, String dietitianName) {
        this.id = id;
        this.email = email;
        this.dietitianId = dietitianId;
        this.dietitianName = dietitianName;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Long getDietitianId() {
        return dietitianId;
    }

    public String getDietitianName() {
        return dietitianName;
    }
}
