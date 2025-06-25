package balancebite.errorHandling;

import java.util.List;

public class MealInDietException extends RuntimeException {
    private final List<String> dietNames;

    public MealInDietException(String message, List<String> dietNames) {
        super(message);
        this.dietNames = dietNames;
    }

    public List<String> getDietNames() {
        return dietNames;
    }
}
