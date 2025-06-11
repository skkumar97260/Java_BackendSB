package com.pixalive.backend.middleware;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class BasicAuthFilter extends OncePerRequestFilter {

    @Value("${basic.auth.user}")
    private String basicUser;

    @Value("${basic.auth.key}")
    private String basicPassword;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            sendJsonErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Basic Auth");
            return;
        }

        String base64Credentials = authHeader.substring("Basic ".length()).trim();
        String credentials = new String(Base64.getDecoder().decode(base64Credentials));
        String[] parts = credentials.split(":", 2);

        if (parts.length != 2 || !parts[0].equals(basicUser) || !parts[1].equals(basicPassword)) {
            sendJsonErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid Basic Auth credentials");
            return;
        }

        filterChain.doFilter(request, response);
    }
    private void sendJsonErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
