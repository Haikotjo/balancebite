
package balancebite.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String[] rawPasswords = {
                "Password123!",
                "Password456@",
                "Password789#",
                "Password101$",
                "Password102%",
                "Password103&"
        };

        String[] userNames = {
                "John Doe",
                "Jane Smith",
                "Tom Brown",
                "Emily White",
                "Haiko White",
                "Mieke White",
        };

        for (int i = 0; i < rawPasswords.length; i++) {
            String hashedPassword = encoder.encode(rawPasswords[i]);
            System.out.println(userNames[i] + "'s hashed password: " + hashedPassword);
        }
    }
}
