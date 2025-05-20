package balancebite.repository;

import balancebite.model.diet.DietPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DietPlanRepository extends JpaRepository<DietPlan, Long> {

    Page<DietPlan> findByCreatedBy_IdOrAdjustedBy_Id(Long createdById, Long adjustedById, Pageable pageable);

    Page<DietPlan> findByCreatedBy_Id(Long userId, Pageable pageable);

    List<DietPlan> findByIsTemplateTrue();

    List<DietPlan> findByIsTemplateTrueAndCreatedBy_IdNot(Long excludedUserId);
}
