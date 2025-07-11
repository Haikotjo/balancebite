package balancebite.repository;

import balancebite.model.user.

        Role;
import balancebite.model.user.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing Role entities.
 * This interface extends JpaRepository to provide basic CRUD operations and
 * additional query methods for the Role entity.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, UserRole> {

    /**
     * Finds a Role entity by its name.
     *
     * @param rolename the name of the role to find
     * @return an Optional containing the Role entity associated with the given name, or empty if not found
     */
    Optional<Role> findByRolename(UserRole rolename);
}
