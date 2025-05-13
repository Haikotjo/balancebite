package balancebite.repository;

import balancebite.model.diet.DietPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DietPlanRepository extends JpaRepository<DietPlan, Long> {
    List<DietPlan> findByCreatedBy_IdOrAdjustedBy_Id(Long createdById, Long adjustedById);
    List<DietPlan> findByIsTemplateTrue();
    List<DietPlan> findByIsTemplateTrueAndCreatedBy_IdNot(Long excludedUserId);
}
