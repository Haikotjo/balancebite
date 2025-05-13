package balancebite.mapper;

import balancebite.dto.diet.DietDayDTO;
import balancebite.dto.diet.DietPlanDTO;
import balancebite.dto.diet.DietPlanInputDTO;
import balancebite.dto.user.PublicUserDTO;
import balancebite.dto.user.UserDTO;
import balancebite.model.diet.DietPlan;
import balancebite.model.user.User;
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

    public DietPlanMapper(DietDayMapper dietDayMapper, UserMapper userMapper) {
        this.dietDayMapper = dietDayMapper;
        this.userMapper = userMapper;
    }

    /**
     * Converts a DietPlan entity to a DietPlanDTO.
     */
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

        return new DietPlanDTO(
                dietPlan.getId(),
                dietPlan.getName(),
                dietPlan.getOriginalDietId(),
                dietPlan.isTemplate(),
                dietPlan.getCreatedAt(),
                dietPlan.getUpdatedAt(),
                createdByDTO,
                adjustedByDTO,
                dietDayDTOs,
                dietPlan.getDietDescription(),
                dietPlan.getDiets()
        );
    }

    /**
     * Converts a DietPlanInputDTO to a new DietPlan entity.
     */
    public DietPlan toEntity(DietPlanInputDTO input, Optional<User> createdBy, Optional<User> adjustedBy) {
        if (input == null) {
            log.warn("Received null DietPlanInputDTO, returning null for DietPlan entity.");
            return null;
        }

        DietPlan dietPlan = new DietPlan();
        dietPlan.setName(input.getName());
        dietPlan.setOriginalDietId(input.getOriginalDietId());
        dietPlan.setTemplate(input.isTemplate());
        dietPlan.setDietDescription(input.getDietDescription());
        dietPlan.setDiets(input.getDiets());

        createdBy.ifPresent(dietPlan::setCreatedBy);
        adjustedBy.ifPresent(dietPlan::setAdjustedBy);

        log.debug("Converted DietPlanInputDTO to new DietPlan entity: {}", dietPlan);
        return dietPlan;
    }

    /**
     * Updates an existing DietPlan entity from DietPlanInputDTO.
     */
    public void updateFromInputDTO(DietPlan dietPlan, DietPlanInputDTO input, Optional<User> createdBy, Optional<User> adjustedBy) {
        if (dietPlan == null || input == null) {
            log.warn("Cannot update DietPlan: entity or input is null.");
            return;
        }

        dietPlan.setName(input.getName());
        dietPlan.setOriginalDietId(input.getOriginalDietId());
        dietPlan.setTemplate(input.isTemplate());
        dietPlan.setDietDescription(input.getDietDescription());
        dietPlan.setDiets(input.getDiets());

        createdBy.ifPresent(dietPlan::setCreatedBy);
        adjustedBy.ifPresent(dietPlan::setAdjustedBy);

        log.debug("Updated DietPlan entity from input: {}", dietPlan);
    }
}
