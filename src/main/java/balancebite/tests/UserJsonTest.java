package balancebite.tests;

import balancebite.model.user.Role;
import balancebite.model.user.User;
import balancebite.model.user.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.Set;

public class UserJsonTest {
    public static void main(String[] args) {
        try {
            // Create a new User object
            User testUser = new User();
            testUser.setUserName("Test User");
            testUser.setEmail("test@example.com");
            testUser.setPassword("password123");

            // Create a Role and assign it to the User
            Role userRole = new Role(UserRole.USER);
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);

            testUser.setRoles(roles);

            // Serialize User object to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonOutput = objectMapper.writeValueAsString(testUser);

            // Print the JSON output
            System.out.println("Serialized JSON:");
            System.out.println(jsonOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
