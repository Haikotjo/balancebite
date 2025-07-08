package balancebite.dto.diet;

import balancebite.dto.user.PublicUserDTO;
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
    private final boolean isPrivate;
    private final boolean isRestricted;
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
    private final Double totalSaturatedFat;
    private final Double totalUnsaturatedFat;
    private final Double totalSugars;
    private final Double avgProtein;
    private final Double avgCarbs;
    private final Double avgFat;
    private final Double avgCalories;
    private final Double avgSaturatedFat;
    private final Double avgUnsaturatedFat;
    private final Double avgSugars;
    private final long saveCount;
    private final long weeklySaveCount;
    private final long monthlySaveCount;

    public DietPlanDTO(Long id,
                       String name,
                       @JsonProperty("originalDietId") Long originalDietId,
                       boolean isTemplate,
                       boolean isPrivate,
                       boolean isRestricted,
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
                       Double totalSaturatedFat,
                       Double totalUnsaturatedFat,
                       Double totalSugars,
                       Double avgProtein,
                       Double avgCarbs,
                       Double avgFat,
                       Double avgCalories,
                       Double avgSaturatedFat,
                       Double avgUnsaturatedFat,
                       Double avgSugars,
                       long saveCount,
                       long weeklySaveCount,
                       long monthlySaveCount) {
        this.id = id;
        this.name = name;
        this.originalDietId = originalDietId;
        this.isTemplate = isTemplate;
        this.isPrivate = isPrivate;
        this.isRestricted = isRestricted;
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
        this.totalSaturatedFat = totalSaturatedFat;
        this.totalUnsaturatedFat = totalUnsaturatedFat;
        this.totalSugars = totalSugars;
        this.avgProtein = avgProtein;
        this.avgCarbs = avgCarbs;
        this.avgFat = avgFat;
        this.avgCalories = avgCalories;
        this.avgSaturatedFat = avgSaturatedFat;
        this.avgUnsaturatedFat = avgUnsaturatedFat;
        this.avgSugars = avgSugars;
        this.saveCount = saveCount;
        this.weeklySaveCount = weeklySaveCount;
        this.monthlySaveCount = monthlySaveCount;
    }

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

    public boolean getIsPrivate() {
        return isPrivate;
    }

    public boolean getIsRestricted() {
        return isRestricted;
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

    public Double getTotalSaturatedFat() {
        return totalSaturatedFat;
    }

    public Double getTotalUnsaturatedFat() {
        return totalUnsaturatedFat;
    }

    public Double getTotalSugars() {
        return totalSugars;
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

    public long getSaveCount() {
        return saveCount;
    }

    public long getWeeklySaveCount() {
        return weeklySaveCount;
    }

    public long getMonthlySaveCount() {
        return monthlySaveCount;
    }

    public Double getAvgSaturatedFat() {
        return avgSaturatedFat;
    }

    public Double getAvgUnsaturatedFat() {
        return avgUnsaturatedFat;
    }

    public Double getAvgSugars() {
        return avgSugars;
    }

}
