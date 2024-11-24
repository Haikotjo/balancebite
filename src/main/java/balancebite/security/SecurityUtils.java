package balancebite.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * SecurityUtils provides utility methods for accessing the current authenticated user's details.
 */
public class SecurityUtils {

    /**
     * Gets the email of the currently authenticated user.
     *
     * @return the email of the currently authenticated user, or null if no user is authenticated
     */
    public static String getCurrentAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername(); // In this case email
            }
        }
        return null;
    }

    /**
     * Gets the ID of the currently authenticated user.
     *
     * @return the ID of the currently authenticated user, or null if no user is authenticated
     */
    public static Long getCurrentAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof MyUserDetails) {
                return ((MyUserDetails) principal).getId();
            }
        }
        return null;
    }
}
