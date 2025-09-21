package balancebite.repository;

import balancebite.model.foodItem.PromotedFoodItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PromotedFoodItemRepository extends JpaRepository<PromotedFoodItem, Long> {

    Optional<PromotedFoodItem> findByFoodItemId(Long foodItemId);
    void deleteByFoodItem_Id(Long foodItemId);
    List<PromotedFoodItem> findByEndDateBefore(LocalDateTime time);

}
