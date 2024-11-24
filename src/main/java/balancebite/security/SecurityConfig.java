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

/**
 * SecurityConfig is a configuration class that sets up security settings for the application.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    /**
     * Constructs a SecurityConfig with the specified JwtService and UserRepository.
     *
     * @param jwtService the JwtService used for handling JWT tokens
     * @param userRepository the UserRepository used for retrieving user data
     */
    public SecurityConfig(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    /**
     * Configures a UserDetailsService bean.
     *
     * @return a UserDetailsService implementation
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return new MyUserDetailsService(this.userRepository);
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
     * @param http the HttpSecurity to configure
     * @return the AuthenticationManager
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
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
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz

                                // Ingredient entity endpoints
                                .requestMatchers(HttpMethod.GET, "/ingredients/admin/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/ingredients/owner").hasAnyAuthority("ADMIN", "OWNER")
                                .requestMatchers(HttpMethod.GET, "/ingredients/owner/ingredient/**").hasAnyAuthority("ADMIN", "OWNER")

                                .requestMatchers(HttpMethod.GET, "/ingredients/owner/menu_item/**").hasAnyAuthority("ADMIN", "OWNER")
                                .requestMatchers(HttpMethod.POST, "/ingredients/admin/create").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/ingredients/owner/**").hasAnyAuthority("ADMIN", "OWNER")
                                .requestMatchers(HttpMethod.PUT, "/ingredients/admin/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/ingredients/owner/**").hasAnyAuthority("ADMIN", "OWNER")
                                .requestMatchers(HttpMethod.DELETE, "/ingredients/admin/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/ingredients/owner/**").hasAnyAuthority("ADMIN", "OWNER")

                                // MenuItem entity endpoints
                                .requestMatchers(HttpMethod.GET, "/menu-items/admin/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/menu-items/owner/**").hasAnyAuthority("ADMIN", "OWNER")
                                .requestMatchers(HttpMethod.GET, "/menu-items/restaurant/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/menu-items/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/menu-items/admin/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/menu-items/owner/**").hasAnyAuthority("OWNER", "ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/menu-items/owner/**").hasAuthority("OWNER")
                                .requestMatchers(HttpMethod.PUT, "/menu-items/admin/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/menu-items/owner/**").hasAnyAuthority("OWNER", "ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/menu-items/admin/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/menu-items/search").permitAll()

                                // Menu entity endpoints
                                .requestMatchers(HttpMethod.GET, "/menus/admin/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/menus/owner/**").hasAnyAuthority("ADMIN", "OWNER")
                                .requestMatchers(HttpMethod.GET, "/menus/restaurant/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/menus/menu/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/menus/search").permitAll()
                                .requestMatchers(HttpMethod.POST, "/menus/admin/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/menus/owner").hasAnyAuthority("OWNER", "ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/menus/owner/**").hasAnyAuthority("OWNER", "ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/menus/admin/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/menus/owner/**").hasAnyAuthority("OWNER", "ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/menus/admin/**").hasAuthority("ADMIN")

                                // Restaurants entity endpoints
                                .requestMatchers(HttpMethod.GET, "/restaurants").permitAll()
                                .requestMatchers(HttpMethod.GET, "/restaurants/search").permitAll()
                                .requestMatchers(HttpMethod.GET, "/restaurants/{id}").permitAll()
                                .requestMatchers(HttpMethod.GET, "/restaurants/admin/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/restaurants/owner/**").hasAnyAuthority("OWNER", "ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/restaurants/admin/**").hasAnyAuthority( "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/restaurants/owner").hasAnyAuthority("OWNER", "ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/restaurants/admin/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/restaurants/owner").hasAnyAuthority("OWNER", "ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/restaurants/admin/**").hasAuthority("ADMIN")

                                // User entity endpoints
                                .requestMatchers(HttpMethod.GET, "/users/profile").authenticated()
                                .requestMatchers(HttpMethod.GET, "/users/admin/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/users/admin/all").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/users/search/by-email").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/users/search/by-role").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/{userId}/address").hasAnyAuthority("ADMIN", "CUSTOMER")
                                .requestMatchers(HttpMethod.GET, "/{userId}/restaurants").hasAnyAuthority("ADMIN", "OWNER")
                                .requestMatchers(HttpMethod.POST, "/users/customer").permitAll()
                                .requestMatchers(HttpMethod.POST, "/users/owner").permitAll()
                                .requestMatchers(HttpMethod.POST, "/auth").permitAll()
                                .requestMatchers(HttpMethod.PUT, "/users/admin/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/users/profile").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/users/role/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/users/profile").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/users/admin/**").hasAuthority("ADMIN")

                                // Role entity endpoints
                                .requestMatchers(HttpMethod.GET, "/roles").hasAuthority("ADMIN")

                                // DeliveryAddress entity endpoints
                                .requestMatchers(HttpMethod.GET, "/address/admin/all").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/address/admin/{id}").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/address/customer").hasAuthority("CUSTOMER")

                                .requestMatchers(HttpMethod.POST, "/address/admin/{userId}").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/address/customer").hasAuthority("CUSTOMER")

                                .requestMatchers(HttpMethod.PUT, "/address/admin/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/address/customer").hasAuthority("CUSTOMER")

                                .requestMatchers(HttpMethod.DELETE, "/address/admin/{id}").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/address/customer").hasAuthority("CUSTOMER")

                                // Notification entity endpoints
                                .requestMatchers(HttpMethod.GET, "/notifications/all").hasAnyAuthority("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/notifications/**").hasAnyAuthority("ADMIN", "OWNER")
                                .requestMatchers(HttpMethod.POST, "/notifications/**").hasAnyAuthority("ADMIN", "OWNER")
                                .requestMatchers(HttpMethod.PUT, "/notifications/**").hasAnyAuthority("ADMIN", "OWNER")
                                .requestMatchers(HttpMethod.DELETE, "/notifications/**").hasAnyAuthority("ADMIN", "OWNER")
                                .requestMatchers(HttpMethod.GET, "/notifications/admin/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/notifications/owner/**").hasAuthority("OWNER")

                                //  Test endpoints
                                .requestMatchers(HttpMethod.GET, "/test/check-expiration-dates").permitAll()

                                // Order entity endpoints
                                .requestMatchers(HttpMethod.GET, "/orders/admin/all").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/orders/admin/{id}").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/orders/customer").hasAuthority("CUSTOMER")
                                .requestMatchers(HttpMethod.GET, "/orders/owner").hasAuthority("OWNER")
                                .requestMatchers(HttpMethod.GET, "/orders/{id}/print").hasAuthority("OWNER")

                                .requestMatchers(HttpMethod.POST).hasAnyAuthority("ADMIN", "CUSTOMER")

                                .requestMatchers(HttpMethod.PUT, "/orders/customer/**").hasAuthority("CUSTOMER")
                                .requestMatchers(HttpMethod.PUT, "/orders/admin/**").hasAuthority("ADMIN")

                                .requestMatchers(HttpMethod.DELETE, "/orders/**").hasAuthority("ADMIN")


                                // Enabling this line will allow ADMIN to access all endpoints.
//                .requestMatchers("/**").hasAnyAuthority("ADMIN")

                                // Add this line to disable security for all endpoints
//                                .anyRequest().permitAll()

                                .anyRequest().authenticated()
                )

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtRequestFilter(jwtService, userDetailsService()), UsernamePasswordAuthenticationFilter.class);
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
