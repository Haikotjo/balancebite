package balancebite.dto;

import java.util.List;

public class FoodItemInputDTO {
    private String name;
    private List<NutrientInfoDTO> nutrients;

    // No-argument constructor
    public FoodItemInputDTO() {}

    // Parameterized constructor
    public FoodItemInputDTO(String name, List<NutrientInfoDTO> nutrients) {
        this.name = name;
        this.nutrients = nutrients;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<NutrientInfoDTO> getNutrients() {
        return nutrients;
    }

    public void setNutrients(List<NutrientInfoDTO> nutrients) {
        this.nutrients = nutrients;
    }
}
