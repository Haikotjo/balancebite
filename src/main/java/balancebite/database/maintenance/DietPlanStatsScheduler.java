package balancebite.database.maintenance;

import balancebite.model.diet.DietPlan;
import balancebite.repository.DietPlanRepository;
import balancebite.repository.SavedDietPlanRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DietPlanStatsScheduler {

    private final DietPlanRepository dietPlanRepository;
    private final SavedDietPlanRepository savedDietPlanRepository;

    public DietPlanStatsScheduler(DietPlanRepository dietPlanRepository,
                                  SavedDietPlanRepository savedDietPlanRepository) {
        this.dietPlanRepository = dietPlanRepository;
        this.savedDietPlanRepository = savedDietPlanRepository;
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void updateSaveCounts() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);

        List<DietPlan> plans = dietPlanRepository.findAll();
        for (DietPlan plan : plans) {
            long total = savedDietPlanRepository.countByDietPlan(plan);
            long weekly = savedDietPlanRepository.countByDietPlanAndTimestampAfter(plan, oneWeekAgo);
            long monthly = savedDietPlanRepository.countByDietPlanAndTimestampAfter(plan, oneMonthAgo);

            plan.setSaveCount(total);
            plan.setWeeklySaveCount(weekly);
            plan.setMonthlySaveCount(monthly);
        }

        dietPlanRepository.saveAll(plans);
    }
}
