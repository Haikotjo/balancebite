package balancebite.dto.user;

import java.time.LocalDate;

public class WeightEntryDTO {
    private final Double weight;
    private final LocalDate date;

    public WeightEntryDTO(Double weight, LocalDate date) {
        this.weight = weight;
        this.date = date;
    }

    public Double getWeight() { return weight; }
    public LocalDate getDate() { return date; }
}