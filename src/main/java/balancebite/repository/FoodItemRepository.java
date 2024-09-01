package balancebite.repository;

import balancebite.model.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for FoodItem entity.
 * Provides methods to perform CRUD operations and custom queries.
 */
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

    /**
     * Finds a list of FoodItems by name, ignoring case.
     *
     * @param name The name to search for.
     * @return A list of FoodItems that contain the specified name.
     */
    List<FoodItem> findByNameContainingIgnoreCase(String name);

    /**
     * Checks if a FoodItem with the specified name exists.
     *
     * @param name The name to check for.
     * @return True if a FoodItem with the specified name exists, false otherwise.
     */
    boolean existsByName(String name);
}
