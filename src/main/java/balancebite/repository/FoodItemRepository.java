package balancebite.repository;

import balancebite.dto.fooditem.FoodItemNameDTO;
import balancebite.model.FoodItem;
import balancebite.model.NutrientInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository interface for FoodItem and NutrientInfo entities.
 * Provides methods to perform CRUD operations and custom queries related to food items and nutrients.
 */
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

    /**
     * Finds a list of FoodItems by name that starts with the specified prefix, ignoring case.
     *
     * @param prefix The prefix to search for.
     * @return A list of FoodItems whose names start with the specified prefix.
     */
    List<FoodItem> findByNameStartingWithIgnoreCase(String prefix);

    /**
     * Checks if a FoodItem with the specified name exists.
     *
     * @param name The name to check for.
     * @return True if a FoodItem with the specified name exists, false otherwise.
     */
    boolean existsByName(String name);

    /**
     * Checks if a FoodItem with the specified FDC ID exists.
     *
     * @param fdcId The FDC ID to check for.
     * @return True if a FoodItem with the specified FDC ID exists, false otherwise.
     */
    boolean existsByFdcId(int fdcId);

    /**
     * Retrieves all food items with only ID and name.
     *
     * @return A list of FoodItemNameDTOs containing only ID and name.
     */
    @Query("SELECT DISTINCT new balancebite.dto.fooditem.FoodItemNameDTO(f.id, f.name) FROM FoodItem f")
    List<FoodItemNameDTO> findAllFoodItemNames();
}
