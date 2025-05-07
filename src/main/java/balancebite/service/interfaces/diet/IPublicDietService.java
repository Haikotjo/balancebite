package balancebite.service.interfaces.diet;

import balancebite.dto.diet.DietDTO;
import java.util.List;

public interface IPublicDietService {
    List<DietDTO> getAllPublicDiets();
    DietDTO getPublicDietById(Long id);
}
