package balancebite.database.maintenance;

import balancebite.model.meal.Meal;
import balancebite.repository.MealRepository;
import balancebite.repository.SavedMealRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class MealStatsScheduler {

    private final MealRepository mealRepository;
    private final SavedMealRepository savedMealRepository;

    public MealStatsScheduler(MealRepository mealRepository,
                              SavedMealRepository savedMealRepository) {
        this.mealRepository = mealRepository;
        this.savedMealRepository = savedMealRepository;
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void updateSaveCounts() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);

        List<Meal> meals = mealRepository.findAll();
        for (Meal meal : meals) {
            long total = savedMealRepository.countByMeal(meal);
            long weekly = savedMealRepository.countByMealAndTimestampAfter(meal, oneWeekAgo);
            long monthly = savedMealRepository.countByMealAndTimestampAfter(meal, oneMonthAgo);

            meal.setSaveCount(total);
            meal.setWeeklySaveCount(weekly);
            meal.setMonthlySaveCount(monthly);
        }

        mealRepository.saveAll(meals);
    }
}
