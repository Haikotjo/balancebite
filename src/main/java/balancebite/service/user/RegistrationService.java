package balancebite.service.user;

import balancebite.dto.user.UserRegistrationInputDTO;
import balancebite.errorHandling.EntityAlreadyExistsException;
import balancebite.model.user.Role;
import balancebite.model.user.User;
import balancebite.model.user.UserRole;
import balancebite.repository.RoleRepository;
import balancebite.repository.UserRepository;
import balancebite.security.JwtService;
import balancebite.security.MyUserDetails;
import balancebite.service.interfaces.IRegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    private final JwtService jwtService;

    /**
     * Constructor for RegistrationService.
     *
     * @param userRepository  The repository for managing User entities.
     * @param roleRepository  The repository for managing Role entities.
     * @param passwordEncoder The encoder for hashing passwords.
     */
    public RegistrationService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Registers a new user and logs them in upon successful registration.
     * If the email already exists, it throws an EntityAlreadyExistsException.
     *
     * @param registrationDTO The DTO containing user registration details.
     * @return A map containing access and refresh tokens.
     */
    @Override
    public Map<String, String> registerUser(UserRegistrationInputDTO registrationDTO) {
        log.info("Attempting to register user with email: {}", registrationDTO.getEmail());

        // Check if the email already exists
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
            Set<Role> roles = registrationDTO.getRoles().stream()
                    .map(roleName -> new Role(UserRole.valueOf(roleName)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        } else {
            user.setRoles(Collections.singleton(new Role(UserRole.USER))); // Default to USER role
        }

        // Save the user to the database
        userRepository.save(user);

        log.info("User registered successfully with email: {}", registrationDTO.getEmail());

        // Generate JWT tokens (direct login)
        MyUserDetails userDetails = new MyUserDetails(user);
        List<String> roles = userDetails.getRoles();

        String accessToken = jwtService.generateAccessToken(
                user.getId(),
                roles,
                user.getUserName(), // Voeg de username toe
                user.getEmail() // Voeg de email toe
        );

        String refreshToken = jwtService.generateRefreshToken(
                user.getId(),
                user.getUserName(), // Voeg de username toe
                user.getEmail() // Voeg de email toe
        );

        log.info("JWT tokens generated for userId: {}", user.getId());

        // Return tokens
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
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
}
