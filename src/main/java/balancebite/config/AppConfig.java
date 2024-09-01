package balancebite.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for defining beans used in the application.
 */
@Configuration
public class AppConfig {

    /**
     * Configures and returns a shared ObjectMapper bean with custom settings.
     *
     * @return The configured ObjectMapper instance.
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // Add support for Java 8 date and time API
        objectMapper.registerModule(new JavaTimeModule());

        // Enable pretty-printing of JSON output
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Disable failure on empty beans
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        return objectMapper;
    }
}
