package balancebite.service;

import balancebite.model.User;
import balancebite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing User entities.
 * Handles creation, retrieval, and deletion of users.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieves all users from the repository.
     *
     * @return a list of all users.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user.
     * @return the user if found, or Optional.empty if not.
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Creates a new user and saves it to the repository.
     *
     * @param user the user to create.
     * @return the created user.
     */
    public User createUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete.
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
