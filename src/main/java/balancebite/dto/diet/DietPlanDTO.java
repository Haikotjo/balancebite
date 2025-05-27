package balancebite.dto.diet;

import balancebite.dto.user.PublicUserDTO;
import balancebite.dto.user.UserDTO;
import balancebite.model.meal.references.Diet;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class DietPlanDTO {

    private final Long id;
    private final String name;
    private final Long originalDietId;
    private final boolean isTemplate;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final PublicUserDTO createdBy;
    private final PublicUserDTO adjustedBy;
    private final List<DietDayDTO> dietDays;
    private final String dietDescription;
    private final Set<Diet> diets;
    private final Double totalProtein;
    private final Double totalCarbs;
    private final Double totalFat;
    private final Double totalCalories;
    private final Double avgProtein;
    private final Double avgCarbs;
    private final Double avgFat;
    private final Double avgCalories;


    public DietPlanDTO(Long id,
                       String name,
                       @JsonProperty("originalDietId") Long originalDietId,
                       boolean isTemplate,
                       LocalDateTime createdAt,
                       LocalDateTime updatedAt,
                       PublicUserDTO createdBy,
                       PublicUserDTO adjustedBy,
                       List<DietDayDTO> dietDays,
                       String dietDescription,
                       Set<Diet> diets,
                       Double totalProtein,
                       Double totalCarbs,
                       Double totalFat,
                       Double totalCalories,
                       Double avgProtein,
                       Double avgCarbs,
                       Double avgFat,
                       Double avgCalories) {
        this.id = id;
        this.name = name;
        this.originalDietId = originalDietId;
        this.isTemplate = isTemplate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.adjustedBy = adjustedBy;
        this.dietDays = (dietDays != null) ? List.copyOf(dietDays) : List.of();
        this.dietDescription = dietDescription;
        this.diets = diets;
        this.totalProtein = totalProtein;
        this.totalCarbs = totalCarbs;
        this.totalFat = totalFat;
        this.totalCalories = totalCalories;
        this.avgProtein = avgProtein;
        this.avgCarbs = avgCarbs;
        this.avgFat = avgFat;
        this.avgCalories = avgCalories;
    }


    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getOriginalDietId() {
        return originalDietId;
    }

    public boolean isTemplate() {
        return isTemplate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public PublicUserDTO getCreatedBy() {
        return createdBy;
    }

    public PublicUserDTO getAdjustedBy() {
        return adjustedBy;
    }

    public List<DietDayDTO> getDietDays() {
        return dietDays;
    }
    public String getDietDescription() {
        return dietDescription;
    }

    public Set<Diet> getDiets() {
        return diets;
    }

    public Double getTotalProtein() {
        return totalProtein;
    }

    public Double getTotalCarbs() {
        return totalCarbs;
    }

    public Double getTotalFat() {
        return totalFat;
    }

    public Double getTotalCalories() {
        return totalCalories;
    }

    public Double getAvgProtein() {
        return avgProtein;
    }

    public Double getAvgCarbs() {
        return avgCarbs;
    }

    public Double getAvgFat() {
        return avgFat;
    }

    public Double getAvgCalories() {
        return avgCalories;
    }
}
