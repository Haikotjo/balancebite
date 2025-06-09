package balancebite.service.interfaces.diet;

import balancebite.dto.diet.DietPlanAdminListDTO;
import java.util.List;

public interface IAdminDietPlanService {

    List<DietPlanAdminListDTO> getAllDietPlansWithCreator();

    void deleteDietPlanById(Long id);
}
