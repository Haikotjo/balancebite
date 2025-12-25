package balancebite.dto;

public record MealImageDTO(
        Long id,
        String imageUrl,
        int orderIndex,
        boolean primary
) {}