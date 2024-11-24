package balancebite.utils;

import balancebite.errorHandling.MissingUserInformationException;
import balancebite.model.user.User;

/**
 * Utility class for validating user information.
 */
public class UserValidationUtil {

    /**
     * Validates that the user has provided all necessary information.
     *
     * @param user The user to validate.
     * @throws MissingUserInformationException If any required information is missing.
     */
    public static void validateUserInformation(User user) {
        if (user.getWeight() == null || user.getHeight() == null || user.getAge() == null ||
                user.getGender() == null || user.getActivityLevel() == null || user.getGoal() == null) {
            throw new MissingUserInformationException(
                    "User must provide all required information: weight, height, age, gender, activity level, and goal. Please update profile."
            );
        }
    }
}
