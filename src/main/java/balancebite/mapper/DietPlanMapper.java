package balancebite.mapper;

import balancebite.dto.diet.DietDayDTO;
import balancebite.dto.diet.DietPlanDTO;
import balancebite.dto.diet.DietPlanInputDTO;
import balancebite.dto.user.PublicUserDTO;
import balancebite.model.diet.DietPlan;
import balancebite.model.user.User;
import balancebite.repository.SavedDietPlanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class DietPlanMapper {

    private static final Logger log = LoggerFactory.getLogger(DietPlanMapper.class);

    private final DietDayMapper dietDayMapper;
    private final UserMapper userMapper;
    private final SavedDietPlanRepository savedDietPlanRepository;

    public DietPlanMapper(DietDayMapper dietDayMapper, UserMapper userMapper, SavedDietPlanRepository savedDietPlanRepository) {
        this.dietDayMapper = dietDayMapper;
        this.userMapper = userMapper;
        this.savedDietPlanRepository = savedDietPlanRepository;
    }

    public DietPlanDTO toDTO(DietPlan dietPlan) {
        if (dietPlan == null) {
            log.warn("Received null DietPlan entity, returning null for DietPlanDTO.");
            return null;
        }

        List<DietDayDTO> dietDayDTOs = dietPlan.getDietDays().stream()
                .map(dietDayMapper::toDTO)
                .toList();

        PublicUserDTO createdByDTO = dietPlan.getCreatedBy() != null
                ? new PublicUserDTO(dietPlan.getCreatedBy().getId(), dietPlan.getCreatedBy().getUserName())
                : null;

        PublicUserDTO adjustedByDTO = dietPlan.getAdjustedBy() != null
                ? new PublicUserDTO(dietPlan.getAdjustedBy().getId(), dietPlan.getAdjustedBy().getUserName())
                : null;

        long saveCount = Optional.ofNullable(dietPlan.getSaveCount()).orElse(0L);
        long weeklySaveCount = Optional.ofNullable(dietPlan.getWeeklySaveCount()).orElse(0L);
        long monthlySaveCount = Optional.ofNullable(dietPlan.getMonthlySaveCount()).orElse(0L);

        return new DietPlanDTO(
                dietPlan.getId(),
                dietPlan.getName(),
                dietPlan.getOriginalDietId(),
                dietPlan.isTemplate(),
                dietPlan.isPrivate(),
                dietPlan.getCreatedAt(),
                dietPlan.getUpdatedAt(),
                createdByDTO,
                adjustedByDTO,
                dietDayDTOs,
                dietPlan.getDietDescription(),
                dietPlan.getDiets(),
                dietPlan.getTotalProtein(),
                dietPlan.getTotalCarbs(),
                dietPlan.getTotalFat(),
                dietPlan.getTotalCalories(),
                dietPlan.getAvgProtein(),
                dietPlan.getAvgCarbs(),
                dietPlan.getAvgFat(),
                dietPlan.getAvgCalories(),
                saveCount,
                weeklySaveCount,
                monthlySaveCount
        );
    }

    public DietPlan toEntity(DietPlanInputDTO input, Optional<User> createdBy, Optional<User> adjustedBy) {
        if (input == null) {
            log.warn("Received null DietPlanInputDTO, returning null for DietPlan entity.");
            return null;
        }

        DietPlan dietPlan = new DietPlan();
        dietPlan.setName(input.getName());
        dietPlan.setOriginalDietId(input.getOriginalDietId());
        dietPlan.setTemplate(input.isTemplate());
        dietPlan.setPrivate(input.isPrivate());
        dietPlan.setDietDescription(input.getDietDescription());
        dietPlan.setDiets(input.getDiets());

        createdBy.ifPresent(dietPlan::setCreatedBy);
        adjustedBy.ifPresent(dietPlan::setAdjustedBy);

        log.debug("Converted DietPlanInputDTO to new DietPlan entity: {}", dietPlan);
        return dietPlan;
    }

    public void updateFromInputDTO(DietPlan dietPlan, DietPlanInputDTO input, Optional<User> createdBy, Optional<User> adjustedBy) {
        if (dietPlan == null || input == null) {
            log.warn("Cannot update DietPlan: entity or input is null.");
            return;
        }

        dietPlan.setName(input.getName());
        dietPlan.setOriginalDietId(input.getOriginalDietId());
        dietPlan.setTemplate(input.isTemplate());
        dietPlan.setPrivate(input.isPrivate());
        dietPlan.setDietDescription(input.getDietDescription());
        dietPlan.setDiets(input.getDiets());

        createdBy.ifPresent(dietPlan::setCreatedBy);
        adjustedBy.ifPresent(dietPlan::setAdjustedBy);

        log.debug("Updated DietPlan entity from input: {}", dietPlan);
    }
}
