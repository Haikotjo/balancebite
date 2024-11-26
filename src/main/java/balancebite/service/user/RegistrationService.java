package balancebite.service.user;

import balancebite.dto.user.UserRegistrationInputDTO;
import balancebite.errorHandling.EntityAlreadyExistsException;
import balancebite.model.user.Role;
import balancebite.model.user.User;
import balancebite.model.user.UserRole;
import balancebite.repository.RoleRepository;
import balancebite.repository.UserRepository;
import balancebite.security.MyUserDetails;
import balancebite.service.interfaces.IRegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class responsible for handling user registration and password management.
 * Also includes methods for fetching user details.
 */
@Service
@Transactional
public class RegistrationService implements IRegistrationService {
    private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor for RegistrationService.
     *
     * @param userRepository  The repository for managing User entities.
     * @param roleRepository  The repository for managing Role entities.
     * @param passwordEncoder The encoder for hashing passwords.
     */
    public RegistrationService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user using the provided UserRegistrationInputDTO.
     *
     * @param registrationDTO The DTO containing user registration details.
     */
    @Override
    public void registerUser(UserRegistrationInputDTO registrationDTO) {
        log.info("Attempting to register user with email: {}", registrationDTO.getEmail());

        // Check if the email already exists in the system
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            log.warn("Registration failed: Email '{}' already exists.", registrationDTO.getEmail());
            throw new EntityAlreadyExistsException("A user with email " + registrationDTO.getEmail() + " already exists.");
        }

        // Hash the password
        String hashedPassword = passwordEncoder.encode(registrationDTO.getPassword());

        // Create a new User entity and populate fields from DTO
        User user = new User();
        user.setEmail(registrationDTO.getEmail());
        user.setPassword(hashedPassword);

        // Use userName from DTO, or default to email if userName is not provided
        user.setUserName(registrationDTO.getUserName() != null && !registrationDTO.getUserName().isBlank()
                ? registrationDTO.getUserName()
                : registrationDTO.getEmail());

        // Handle roles: assign roles from DTO or default to USER role
        if (registrationDTO.getRoles() != null && !registrationDTO.getRoles().isEmpty()) {
            // Convert role names (String) from DTO into Role entities
            Set<Role> roles = registrationDTO.getRoles().stream()
                    .map(roleName -> new Role(UserRole.valueOf(roleName))) // Convert String to Role
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        } else {
            user.setRoles(Collections.singleton(new Role(UserRole.USER))); // Default to USER role
        }

        // Process verification token for elevated roles
        if (registrationDTO.getVerificationToken() != null) {
            log.info("Processing verification token for user with email: {}", registrationDTO.getEmail());
            // Add your verification token logic here
        }

        try {
            // Save the user to the database
            userRepository.save(user);
            log.info("Successfully registered user with email: {}", registrationDTO.getEmail());
        } catch (Exception e) {
            log.error("Unexpected error during user registration for email '{}': {}", registrationDTO.getEmail(), e.getMessage(), e);
            throw new RuntimeException("An unexpected error occurred while registering the user. Please try again.");
        }
    }

    /**
     * Loads a user by their email address.
     *
     * @param email The email address of the user.
     * @return The UserDetails object representing the user.
     * @throws UsernameNotFoundException if the user is not found.
     */
    public UserDetails loadUserByEmail(String email) {
        log.info("Loading user by email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " not found"));
        return new MyUserDetails(user); // Assuming MyUserDetails is your custom UserDetails implementation.
    }

//    /**
//     * Retrieves a Role entity by its name from the database.
//     *
//     * @param roleName The name of the role to retrieve.
//     * @return The Role entity associated with the given name.
//     * @throws RuntimeException if the role is not found in the database.
//     */
//    private Role getRoleByName(UserRole roleName) {
//        return roleRepository.findByRolename(roleName)
//                .orElseThrow(() -> new RuntimeException("Role " + roleName + " not found in the database."));
//    }
}
