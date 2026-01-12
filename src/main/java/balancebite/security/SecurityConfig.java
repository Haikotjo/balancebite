package balancebite.security;

import balancebite.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.Customizer;


/**
 * SecurityConfig is a configuration class that sets up security settings for the application.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtService jwtService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(JwtService jwtService,
                          CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
                          JwtRequestFilter jwtRequestFilter) {
        this.jwtService = jwtService;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    /**
     * Configures a PasswordEncoder bean.
     *
     * @return a PasswordEncoder implementation
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures an AuthenticationManager bean.
     *
     * @param http                the HttpSecurity to configure
     * @param passwordEncoder     the PasswordEncoder to use
     * @param userDetailsService  the UserDetailsService to use
     * @return the AuthenticationManager
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder,
                                                       UserDetailsService userDetailsService) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    /**
     * Configures the security filter chain.
     *
     * @param http the HttpSecurity to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz

                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // preflight toestaan

                                .requestMatchers(HttpMethod.GET, "/").permitAll()

                                .requestMatchers(HttpMethod.GET, "/admins/users").hasAnyRole("ADMIN", "DIETITIAN")
                                .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()

                                // register endpoints
                                .requestMatchers(HttpMethod.POST, "/auth/**").permitAll()

                                // user entity endpoints
                                .requestMatchers(HttpMethod.GET, "/users/**").authenticated()

                                .requestMatchers(HttpMethod.GET, "/users/profile").authenticated()
                                .requestMatchers(HttpMethod.PATCH, "/users/basic-info").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/users/weight").authenticated()

                                .requestMatchers(HttpMethod.POST, "/users/create-meal").authenticated()
                                .requestMatchers(HttpMethod.PATCH, "/users/update-meal/{mealId}").authenticated()
                                .requestMatchers(HttpMethod.PATCH, "/users/add-meal/{mealId}").authenticated()

                                .requestMatchers(HttpMethod.GET, "/users/meals**").authenticated()
                                .requestMatchers(HttpMethod.GET, "/users/created-meals").permitAll()
                                .requestMatchers(HttpMethod.GET, "/users/meal/{mealId}").authenticated()

                                .requestMatchers(HttpMethod.DELETE, "/users/meal/{mealId}").authenticated()

                                .requestMatchers(HttpMethod.POST, "/users/consume-meal/{mealId}").authenticated()

                                .requestMatchers(HttpMethod.PATCH, "/users/**").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/users/**").authenticated()
                                .requestMatchers("/admins/**").hasRole("ADMIN")

                                .requestMatchers(HttpMethod.POST, "/daily-intake/user").authenticated()
                                .requestMatchers(HttpMethod.POST, "/user/profile").authenticated()

                                // meal entity endpoints
                                .requestMatchers(HttpMethod.GET, "/meals**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/meals/names").permitAll()
                                .requestMatchers(HttpMethod.GET, "/meals-admin/all").hasAnyRole("ADMIN", "CHEF")
                                .requestMatchers(HttpMethod.GET, "/meals-admin/meal/{id}").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/meals/{id}").permitAll()
                                .requestMatchers(HttpMethod.GET, "/meals/nutrients/{id}").permitAll()
                                .requestMatchers(HttpMethod.GET, "/meals/nutrients-per-food-item/{id}").permitAll()
                                .requestMatchers(HttpMethod.GET, "/meals/sorted").permitAll()
                                .requestMatchers(HttpMethod.PATCH, "/users/update-meal/**").authenticated()

                                .requestMatchers(HttpMethod.POST, "/meals-admin/create-meal").hasAnyRole("ADMIN", "CHEF")
                                .requestMatchers(HttpMethod.PATCH, "/meals-admin/update-meal").hasAnyRole("ADMIN", "CHEF")
                                .requestMatchers(HttpMethod.PATCH, "/meals-admin/add-meal/{mealId}").hasAnyRole("ADMIN", "CHEF")
                                .requestMatchers(HttpMethod.DELETE, "/meals-admin/delete-meal/").hasAnyRole("ADMIN", "CHEF")
                                // meal restriction endpoint
                                .requestMatchers(HttpMethod.PATCH, "/users/meals/{mealId}/restriction").hasAnyRole("RESTAURANT", "DIETITIAN")

                                // FoodItem entity endpoints
                                .requestMatchers(HttpMethod.POST,  "/fooditems").hasAnyRole("ADMIN","SUPERMARKET")
                                .requestMatchers(HttpMethod.PATCH, "/fooditems/**").hasAnyRole("ADMIN","SUPERMARKET")
                                .requestMatchers(HttpMethod.PATCH, "/api/food-items/*/price").hasAnyRole("ADMIN","SUPERMARKET")


                        // ---- AUTHENTICATED EERST (specifieker dan wildcard) ----
                                .requestMatchers(HttpMethod.GET,  "/fooditems/fetch/**").hasAnyRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/fooditems/bulk-fetch-items").hasAnyRole("ADMIN")

                        // ---- PUBLIEK (permitAll) ----
                                .requestMatchers(HttpMethod.GET, "/fooditems/promoted-by-source").permitAll()
                                .requestMatchers(HttpMethod.GET, "/fooditems/promoted-by-category").permitAll()
                                .requestMatchers(HttpMethod.GET, "/fooditems/sources").permitAll()
                                .requestMatchers(HttpMethod.GET, "/fooditems/search-by-name").permitAll()
                                .requestMatchers(HttpMethod.GET, "/fooditems").permitAll()
                                .requestMatchers(HttpMethod.GET, "/fooditems/**").permitAll()


                                // diet entity endpoints
                                .requestMatchers(HttpMethod.GET, "/public/**").permitAll()

                                .requestMatchers(HttpMethod.GET, "/users/diet-plans/**").authenticated()
                                .requestMatchers(HttpMethod.POST, "/users/diet-plans/**").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/users/diet-plans/**").authenticated()
                                .requestMatchers(HttpMethod.PATCH, "/users/diet-plans/**").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/users/diet-plans/**").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/users/update-diet-plans/**").authenticated()
                                // diet restriction endpoint
                                .requestMatchers(HttpMethod.PATCH, "/users/diet-plans/{dietPlanId}/restriction").hasAnyRole("RESTAURANT", "DIETITIAN")

                                // sticky item endpoints
                                .requestMatchers(HttpMethod.POST, "/admin/sticky-items").hasRole("ADMIN")

                                .requestMatchers(HttpMethod.GET, "/sticky-items").permitAll()
                                .requestMatchers(HttpMethod.GET, "/sticky-items/all").permitAll()
                                .requestMatchers(HttpMethod.GET, "/sticky-items/latest").permitAll()

                                // admin-only endpoints
                                .requestMatchers("/admin/dietplans/**").hasRole("ADMIN")

                                // promotions (ADMIN + SUPERMARKET)
                                .requestMatchers(HttpMethod.POST,   "/admin/promotions").hasAnyRole("ADMIN","SUPERMARKET")
                                .requestMatchers(HttpMethod.PUT,    "/admin/promotions/**").hasAnyRole("ADMIN","SUPERMARKET")
                                .requestMatchers(HttpMethod.DELETE, "/admin/promotions/**").hasAnyRole("ADMIN","SUPERMARKET")


                                // DIETITIAN-only endpoints
                                .requestMatchers(HttpMethod.POST, "/user/dietitian/invite-client").hasRole("DIETITIAN")
                                .requestMatchers(HttpMethod.POST, "/user/dietitian/create-meal").hasAnyRole("DIETITIAN", "ADMIN")

                                // DIETITIAN-only endpoints
                                .requestMatchers(HttpMethod.POST, "/user/dietitian/create-dietplan").hasAnyRole("DIETITIAN", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/user/dietitian/add-meal-access").hasAnyRole("DIETITIAN", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/user/dietitian/add-dietplan-access").hasAnyRole("DIETITIAN", "ADMIN")





                                .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception.authenticationEntryPoint(customAuthenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}




//  Turn all above off and below on to disable security


//package nl.novi.eindopdrachtbackend.security;
//
//import nl.novi.eindopdrachtbackend.repository.UserRepository;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
///**
// * SecurityConfig is a configuration class that sets up security settings for the application.
// */
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    private final UserRepository userRepository;
//
//    /**
//     * Constructs a SecurityConfig with the specified UserRepository.
//     *
//     * @param userRepository the UserRepository used for retrieving user data
//     */
//    public SecurityConfig(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    /**
//     * Configures a UserDetailsService bean.
//     *
//     * @return a UserDetailsService implementation
//     */
//    @Bean
//    public UserDetailsService userDetailsService() {
//        return new MyUserDetailsService(this.userRepository);
//    }
//
//    /**
//     * Configures a PasswordEncoder bean.
//     *
//     * @return a PasswordEncoder implementation
//     */
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    /**
//     * Configures an AuthenticationManager bean.
//     *
//     * @param http the HttpSecurity to configure
//     * @return the AuthenticationManager
//     * @throws Exception if an error occurs during configuration
//     */
//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
//        AuthenticationManagerBuilder authenticationManagerBuilder =
//                http.getSharedObject(AuthenticationManagerBuilder.class);
//        authenticationManagerBuilder.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
//        return authenticationManagerBuilder.build();
//    }
//
//    /**
//     * Configures the security filter chain to allow all requests without authentication.
//     *
//     * @param http the HttpSecurity to configure
//     * @return the configured SecurityFilterChain
//     * @throws Exception if an error occurs during configuration
//     */
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(authz -> authz
//                        .anyRequest().permitAll() // Allow all requests without authentication
//                )
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//        return http.build();
//    }
//}
