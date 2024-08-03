package balancebite.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConfig {

    public String getUsdaApiKey() {
        return System.getenv("USDA_API_KEY");
    }
}