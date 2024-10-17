package balancebite.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class for defining beans and retrieving API keys.
 * This class provides a RestTemplate and a WebClient bean, and a method to access the USDA API key.
 */
@Configuration
public class ApiConfig {

    @Value("${usda.api.key}")
    private String usdaApiKey;

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
     * Creates and configures a WebClient bean.
     * WebClient is used to make asynchronous HTTP requests in the application.
     *
     * @return A configured WebClient instance.
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://api.nal.usda.gov/fdc")
                .defaultHeader("api_key", usdaApiKey)
                .build();
    }

    /**
     * Retrieves the USDA API key from application properties.
     * The API key is used to authenticate requests to the USDA FoodData Central API.
     *
     * @return The USDA API key as a String.
     */
    public String getUsdaApiKey() {
        return usdaApiKey;
    }
}
