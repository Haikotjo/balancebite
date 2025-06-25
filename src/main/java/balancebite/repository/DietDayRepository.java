package balancebite.repository;

import balancebite.model.diet.DietDay;
import balancebite.model.meal.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DietDayRepository extends JpaRepository<DietDay, Long> {
    List<DietDay> findByDate(LocalDate date);
    List<DietDay> findByDietId(Long dietId);

    @Query("SELECT dd FROM DietDay dd JOIN FETCH dd.diet WHERE :meal MEMBER OF dd.meals")
    List<DietDay> findByMealsContainingWithDietFetched(@Param("meal") Meal meal);

    List<DietDay> findAllByMealsContaining(Meal meal);

    boolean existsByMealsContaining(Meal meal);

    @Query("SELECT DISTINCT dd.diet.id FROM DietDay dd WHERE :meal MEMBER OF dd.meals")
    List<Long> findDietIdsByMeal(@Param("meal") Meal meal);

    @Query("SELECT DISTINCT dd.diet.name FROM DietDay dd WHERE :meal MEMBER OF dd.meals")
    List<String> findDietNamesByMeal(@Param("meal") Meal meal);

}
