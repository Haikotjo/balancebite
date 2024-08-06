package balancebite.dto;

import java.util.List;

public class FoodItemDTO {
    private Long id;
    private String name;
    private List<NutrientInfoDTO> nutrients;

    // No-argument constructor
    public FoodItemDTO() {}

    // Parameterized constructor
    public FoodItemDTO(Long id, String name, List<NutrientInfoDTO> nutrients) {
        this.id = id;
        this.name = name;
        this.nutrients = nutrients;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
