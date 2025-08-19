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
                registry.addMapping("/**")
                        // Alleen lokaal (origineel):
//                        .allowedOrigins("http://localhost:5173")

                        // Voor productie ook je frontend-URL toestaan, bijvoorbeeld:
                        .allowedOrigins(
                                "https://balancebite-frontend.vercel.app",
                                "http://localhost:5173"
                        )

                        // ↑ En comment de regel erboven dan uit

                        .allowedMethods("GET","POST","PUT","DELETE","PATCH","OPTIONS","HEAD")
                        .allowCredentials(true)
                        .allowedHeaders("*");
            }
        };
    }
}
