package balancebite.repository;

import balancebite.model.MealIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing MealIngredient entities.
 * Extends JpaRepository to provide basic CRUD operations on MealIngredient entities.
 */
public interface MealIngredientRepository extends JpaRepository<MealIngredient, Long> {
}
