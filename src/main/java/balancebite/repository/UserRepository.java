package balancebite.repository;

import balancebite.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
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
     * Finds a user by their username.
     *
     * @param userName The username of the user to find.
     * @return An Optional containing the User if found, or empty if no user exists with the given username.
     */
    Optional<User> findByUserName(String userName);
}
