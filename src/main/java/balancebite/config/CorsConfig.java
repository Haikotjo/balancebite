package balancebite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Allow all endpoints
                        .allowedOrigins("http://localhost:5173") // Allow only your frontend URL
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH") // Specify allowed HTTP methods
                        .allowCredentials(true) // Allow cookies or authentication credentials
                        .allowedHeaders("*"); // Allow all headers
            }
        };
    }
}
