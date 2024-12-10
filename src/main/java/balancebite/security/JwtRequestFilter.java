package balancebite.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JwtRequestFilter processes incoming requests to validate JWT tokens.
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    private final JwtService jwtService;
    private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);

    /**
     * Constructs a JwtRequestFilter with the given JwtService.
     *
     * @param jwtService The JwtService used to process JWT tokens.
     */
    public JwtRequestFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        String jwt = null;
        Long userId = null;

        // Skip auth endpoints
        String path = request.getRequestURI();
        if (path.startsWith("/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7); // Extract JWT token
                userId = jwtService.extractUserId(jwt); // Extract user ID from token
                log.debug("JWT extracted with userId: {}", userId);
            } else {
                log.warn("Authorization header missing or invalid for request: {}", request.getRequestURI());
            }

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = ((MyUserDetailsService) userDetailsService).loadUserById(userId);

                if (jwtService.validateAccessToken(jwt)) {
                    List<String> roles = jwtService.extractRoles(jwt);
                    log.debug("Roles extracted from token: {}", roles);

                    // Map roles to authorities
                    List<org.springframework.security.core.authority.SimpleGrantedAuthority> authorities =
                            roles.stream()
                                    .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role))
                                    .collect(Collectors.toList());

                    // Create an authentication token with roles
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, jwt, authorities
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    log.debug("Authentication set for userId: {} with roles: {}", userId, roles);
                } else {
                    log.warn("JWT validation failed for token: {}", jwt);
                }
            }

        } catch (Exception e) {
            log.error("Error occurred while processing the JWT token: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }
}