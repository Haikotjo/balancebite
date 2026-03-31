package balancebite.dto.diet;

public class DietPlanAdminListDTO {
    private final Long id;
    private final String name;
    private final String creatorName;
    private final String adjustedByName;
    private final boolean isTemplate;

    public DietPlanAdminListDTO(Long id, String name, String creatorName, String adjustedByName, boolean isTemplate) {
        this.id = id;
        this.name = name;
        this.creatorName = creatorName;
        this.adjustedByName = adjustedByName;
        this.isTemplate = isTemplate;
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

    public boolean isTemplate() {
        return isTemplate;
    }
}
