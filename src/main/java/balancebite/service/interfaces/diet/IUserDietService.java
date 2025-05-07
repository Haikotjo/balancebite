package balancebite.service.interfaces.diet;

import balancebite.dto.diet.DietDTO;
import balancebite.dto.diet.DietInputDTO;
import balancebite.dto.user.UserDTO;

import java.util.List;

public interface IUserDietService {

    DietDTO createDiet(DietInputDTO input, Long userId);

    DietDTO getDietById(Long dietId, Long userId);

    List<DietDTO> getAllDietsForUser(Long userId);

    DietDTO updateDiet(Long dietId, DietInputDTO input, Long adjustedByUserId);

    void deleteDiet(Long dietId, Long userId);

    UserDTO addDietToUser(Long userId, Long dietId);
}
