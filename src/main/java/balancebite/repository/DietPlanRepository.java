package balancebite.repository;
import balancebite.dto.diet.DietPlanNameDTO;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import balancebite.model.diet.DietPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DietPlanRepository extends JpaRepository<DietPlan, Long>, JpaSpecificationExecutor<DietPlan> {

    Page<DietPlan> findByAdjustedBy_Id(Long userId, Pageable pageable);

    Page<DietPlan> findByCreatedBy_Id(Long userId, Pageable pageable);

    Page<DietPlan> findByIsTemplateTrue(Pageable pageable);

    Page<DietPlan> findByIsTemplateTrueAndCreatedBy_IdNot(Long excludedUserId, Pageable pageable);

    @Query("SELECT DISTINCT new balancebite.dto.diet.DietPlanNameDTO(d.id, d.name) " +
            "FROM DietPlan d " +
            "WHERE d.isTemplate = true AND d.isPrivate = false")
    List<DietPlanNameDTO> findAllTemplateDietPlanNames();
}
