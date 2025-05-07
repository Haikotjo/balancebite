package balancebite.mapper;

import balancebite.dto.diet.DietDTO;
import balancebite.dto.diet.DietDayDTO;
import balancebite.dto.diet.DietDayInputDTO;
import balancebite.dto.diet.DietInputDTO;
import balancebite.dto.user.UserDTO;
import balancebite.model.diet.Diet;
import balancebite.model.diet.DietDay;
import balancebite.model.user.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DietMapper {

    private final DietDayMapper dietDayMapper;
    private final UserMapper userMapper;

    public DietMapper(DietDayMapper dietDayMapper, UserMapper userMapper) {
        this.dietDayMapper = dietDayMapper;
        this.userMapper = userMapper;
    }

    public DietDTO toDTO(Diet diet) {
        if (diet == null) return null;

        List<DietDayDTO> dietDayDTOs = diet.getDietDays().stream()
                .map(dietDayMapper::toDTO)
                .toList();

        UserDTO createdByDTO = diet.getCreatedBy() != null ? userMapper.toDTO(diet.getCreatedBy()) : null;
        UserDTO adjustedByDTO = diet.getAdjustedBy() != null ? userMapper.toDTO(diet.getAdjustedBy()) : null;

        return new DietDTO(
                diet.getId(),
                diet.getName(),
                diet.getOriginalDietId(),
                diet.isTemplate(),
                diet.getVersion(),
                createdByDTO,
                adjustedByDTO,
                dietDayDTOs
        );
    }

    public void updateFromInputDTO(Diet diet, DietInputDTO input, Optional<User> createdBy, Optional<User> adjustedBy) {
        if (diet == null || input == null) return;

        diet.setName(input.getName());
        diet.setOriginalDietId(input.getOriginalDietId());
        diet.setTemplate(input.isTemplate());

        createdBy.ifPresent(diet::setCreatedBy);
        adjustedBy.ifPresent(diet::setAdjustedBy);
    }
}
