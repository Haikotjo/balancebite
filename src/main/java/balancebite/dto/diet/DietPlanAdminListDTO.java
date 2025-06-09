package balancebite.dto.diet;

public class DietPlanAdminListDTO {
    private final Long id;
    private final String name;
    private final String creatorName;
    private final String adjustedByName;

    public DietPlanAdminListDTO(Long id, String name, String creatorName, String adjustedByName) {
        this.id = id;
        this.name = name;
        this.creatorName = creatorName;
        this.adjustedByName = adjustedByName;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public String getAdjustedByName() {
        return adjustedByName;
    }
}
