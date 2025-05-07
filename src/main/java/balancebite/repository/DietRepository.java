package balancebite.repository;

import balancebite.model.diet.Diet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DietRepository extends JpaRepository<Diet, Long> {
    List<Diet> findByCreatedBy_IdOrAdjustedBy_Id(Long createdById, Long adjustedById);
    List<Diet> findByIsTemplateTrue();
    List<Diet> findByIsTemplateTrueAndCreatedBy_IdNot(Long excludedUserId);
}
