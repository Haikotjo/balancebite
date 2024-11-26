package balancebite.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtRequestFilter is a filter that processes incoming requests to validate JWT tokens.
 * It extends the OncePerRequestFilter to ensure it is executed once per request.
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);

    /**
     * Constructs a JwtRequestFilter with the given JwtService and UserDetailsService.
     *
     * @param jwtService the JwtService used to process JWT tokens
     * @param userDetailsService the UserDetailsService used to load user-specific data
     */
    public JwtRequestFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Filters incoming requests to validate JWT tokens and set the authentication context.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        String email = null;
        String jwt = null;

        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                email = jwtService.extractEmail(jwt);
                log.debug("JWT extracted successfully. Email: {}", email);
            }

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

                if (jwtService.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    log.debug("Authentication set for user: {}", email);
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
