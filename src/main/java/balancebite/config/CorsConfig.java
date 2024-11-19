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
                registry.addMapping("/**") // Sta alle endpoints toe
                        .allowedOrigins("http://localhost:5173") // Sta alleen je frontend-URL toe
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH"); // Specificeer toegestane HTTP-methodes
            }
        };
    }
}
