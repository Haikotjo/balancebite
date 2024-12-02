package balancebite.utils;

import balancebite.dto.user.UserRegistrationInputDTO;
import balancebite.errorHandling.EntityAlreadyExistsException;
import balancebite.errorHandling.UserNotFoundException;
import balancebite.model.user.Role;
import balancebite.model.user.User;
import balancebite.model.user.UserRole;
import balancebite.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserUpdateHelper {
    private final UserRepository userRepository;

    public UserUpdateHelper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User fetchUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Cannot update user: No user found with email " + email));
    }

    public User fetchUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Cannot update user: No user found with ID " + userId));
    }

    public void validateUniqueEmail(String email, Long currentUserId) {
        if (userRepository.existsByEmail(email) &&
                !userRepository.findByEmail(email).get().getId().equals(currentUserId)) {
            throw new EntityAlreadyExistsException("The email " + email + " is already in use by another account.");
        }
    }

    public Set<Role> validateAndConvertRoles(Set<String> roleNames) {
        return roleNames.stream()
                .map(roleName -> {
                    try {
                        return new Role(UserRole.valueOf(roleName)); // Validate against Enum
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Invalid role provided: " + roleName);
                    }
                })
                .collect(Collectors.toSet());
    }

    public User updateBasicInfo(User user, UserRegistrationInputDTO dto) {
        if (dto.getUserName() != null) {
            user.setUserName(dto.getUserName());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        return user;
    }
}
