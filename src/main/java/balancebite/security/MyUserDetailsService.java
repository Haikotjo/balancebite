package balancebite.security;

import balancebite.model.user.User;
import balancebite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * MyUserDetailsService implements the UserDetailsService interface
 * to provide user details for authentication.
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructs a MyUserDetailsService with the specified UserRepository.
     *
     * @param userRepository the UserRepository used to retrieve user data
     */
    @Autowired
    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads the user by username (email in this case).
     * This method remains for backward compatibility with Spring Security's default behavior.
     *
     * @param email the email of the user
     * @return UserDetails for authentication
     */
    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return new MyUserDetails(user);
    }

    /**
     * Loads the user by their ID.
     * This is used for JWT token-based authentication.
     *
     * @param userId the ID of the user
     * @return UserDetails for authentication
     */
    public UserDetails loadUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return new MyUserDetails(user);
    }
}
