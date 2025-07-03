package balancebite.service.fooditem;

import balancebite.repository.PromotedFoodItemRepository;
import balancebite.model.foodItem.PromotedFoodItem;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PromotionCleanupService {

    private final PromotedFoodItemRepository promotedFoodItemRepository;

    public PromotionCleanupService(PromotedFoodItemRepository promotedFoodItemRepository) {
        this.promotedFoodItemRepository = promotedFoodItemRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?") // elke dag om 00:00
    @Transactional
    public void removeExpiredPromotions() {
        LocalDateTime now = LocalDateTime.now();
        List<PromotedFoodItem> expired = promotedFoodItemRepository.findByEndDateBefore(now);
        promotedFoodItemRepository.deleteAll(expired);
    }
}
