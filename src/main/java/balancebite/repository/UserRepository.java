package balancebite.repository;

import balancebite.model.diet.DietPlan;
import balancebite.model.meal.Meal;
import balancebite.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on User entities.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Checks if a user exists with the given email address.
     *
     * @param email The email address to check for existence.
     * @return true if a user with the given email address exists, false otherwise.
     */
    boolean existsByEmail(String email);

    /**
     * Finds a user by their email address.
     *
     * @param email the email address to search for
     * @return an Optional containing the found user, or empty if no user found
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by their username.
     *
     * @param userName The username of the user to find.
     * @return An Optional containing the User if found, or empty if no user exists with the given username.
     */
    Optional<User> findByUserName(String userName);

    @Query("SELECT u FROM User u JOIN u.meals m WHERE m = :meal")
    List<User> findAllByMealsContaining(@Param("meal") Meal meal);

    List<User> findAllBySavedMealsContaining(Meal meal);

}
