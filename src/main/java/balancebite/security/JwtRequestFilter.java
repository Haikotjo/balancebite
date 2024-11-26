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

/**
 * JwtRequestFilter processes incoming requests to validate JWT tokens.
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService; // Gebruik veldinjectie

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

    // Rest van de code blijft hetzelfde, inclusief de aangepaste exception handling
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        // Overslaan van /auth/* endpoints
        String path = request.getRequestURI();
        if (path.startsWith("/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");
        String email = null;
        String jwt = null;

        try {
            // Check of de Authorization header aanwezig is en begint met "Bearer "
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7); // Haal JWT token op
                email = jwtService.extractEmail(jwt); // Haal het emailadres uit de JWT
                log.debug("JWT extracted for email: {}", email);
            }

            // Als email is gevonden en er nog geen authenticatie is ingesteld
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

                // Valideer de token
                if (jwtService.validateToken(jwt, userDetails)) {
                    // Stel de authenticatie in
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
            // Laat de exception doorgaan zodat Spring Security deze kan afhandelen
            log.error("Error occurred while processing the JWT token: {}", e.getMessage(), e);
            throw e;
        }

        // Ga verder met de filter chain
        filterChain.doFilter(request, response);
    }
}
