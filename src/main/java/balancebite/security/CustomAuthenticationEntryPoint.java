package balancebite.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

/**
 * CustomAuthenticationEntryPoint is a custom implementation of the AuthenticationEntryPoint
 * interface, used for handling authentication exceptions in Spring Security.
 *
 * When an unauthenticated user attempts to access a protected resource, this entry point
 * sends a 401 Unauthorized response with a custom error message.
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Handles unauthorized access attempts by sending a 401 Unauthorized response
     * with a custom error message.
     *
     * @param request       the HTTP request
     * @param response      the HTTP response
     * @param authException the exception that triggered the entry point
     * @throws IOException if an input or output error occurs while writing the response
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // Set the HTTP status code to 401 (Unauthorized)
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Write a custom error message to the response
        response.getWriter().write("Authentication failed: " + authException.getMessage());
    }
}
