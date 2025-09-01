package balancebite.repository;

import balancebite.model.MealIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MealIngredientRepository extends JpaRepository<MealIngredient, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM MealIngredient mi WHERE mi.meal.id = :mealId")
    void deleteAllByMealId(@Param("mealId") Long mealId);
}
