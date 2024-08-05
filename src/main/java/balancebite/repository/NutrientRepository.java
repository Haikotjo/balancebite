package balancebite.repository;

import balancebite.model.Nutrient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NutrientRepository extends JpaRepository<Nutrient, Long> {
    List<Nutrient> findByNutrientNameContainingIgnoreCase(String nutrientName);
}
