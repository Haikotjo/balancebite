package balancebite.repository;

import balancebite.model.meal.mealImage.MealImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealImageRepository extends JpaRepository<MealImage, Long> {
    long countByPublicId(String publicId);

}

