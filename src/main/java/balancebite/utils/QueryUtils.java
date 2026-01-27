package balancebite.utils;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

public class QueryUtils {
    private static final Logger log = LoggerFactory.getLogger(QueryUtils.class);

    public static Sort buildSort(String sortBy, String sortOrder, Pageable pageable) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        if (sortBy == null || sortBy.isBlank()) {
            return pageable.getSort().isSorted() ? pageable.getSort() : Sort.by(direction, "name");
        }

        String sortField = switch (sortBy.toLowerCase()) {
            case "calories" -> "totalCalories";
            case "protein" -> "totalProtein";
            case "fat" -> "totalFat";
            case "carbs" -> "totalCarbs";
            case "savecount" -> "saveCount";
            case "weeklysavecount" -> "weeklySaveCount";
            case "monthlysavecount" -> "monthlySaveCount";
            default -> "name";
        };

        return Sort.by(direction, sortField);
    }

    public static <E extends Enum<E>> List<E> parseEnumList(List<String> values, Class<E> enumClass, String label) {
        if (values == null || values.isEmpty()) {
            return Collections.emptyList();
        }
        List<E> parsedValues = new ArrayList<>();
        for (String value : values) {
            if (value == null || value.isBlank()) continue;
            try {
                parsedValues.add(Enum.valueOf(enumClass, value.toUpperCase()));
            } catch (IllegalArgumentException ex) {
                log.warn("Invalid {} filter value: {}", label, value);
            }
        }
        return parsedValues;
    }
}