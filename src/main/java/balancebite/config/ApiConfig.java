package balancebite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for defining beans and retrieving API keys.
 * This class provides a RestTemplate bean and a method to access the USDA API key.
 */
@Configuration
public class ApiConfig {

    /**
     * Creates and configures a RestTemplate bean.
     * RestTemplate is used to make HTTP requests in the application.
     *
     * @return A configured RestTemplate instance.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Retrieves the USDA API key from the system environment variables.
     * The API key is used to authenticate requests to the USDA FoodData Central API.
     *
     * @return The USDA API key as a String.
     */
    public String getUsdaApiKey() {
        return System.getenv("USDA_API_KEY");
    }
}
