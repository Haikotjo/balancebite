package balancebite.errorHandling;

import java.util.List;
import java.util.Map;

public class MealStillInUseException extends RuntimeException {
    private final List<Map<String, Object>> diets;

    public MealStillInUseException(String message, List<Map<String, Object>> diets) {
        super(message);
        this.diets = diets;
    }

    public List<Map<String, Object>> getDiets() {
        return diets;
    }
}
