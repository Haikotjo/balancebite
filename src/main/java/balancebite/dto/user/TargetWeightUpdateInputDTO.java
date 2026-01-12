package balancebite.dto.user;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

public class TargetWeightUpdateInputDTO {

    @NotNull(message = "Target weight must be provided.")
    @Digits(integer = 3, fraction = 2, message = "Invalid weight format.")
    private Double targetWeight;

    public TargetWeightUpdateInputDTO() {}

    public TargetWeightUpdateInputDTO(Double targetWeight) {
        this.targetWeight = targetWeight;
    }

    public Double getTargetWeight() { return targetWeight; }
    public void setTargetWeight(Double targetWeight) { this.targetWeight = targetWeight; }
}