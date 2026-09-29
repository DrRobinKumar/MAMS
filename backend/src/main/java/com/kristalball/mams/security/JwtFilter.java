package com.kristalball.mams.security;

import com.kristalball.mams.model.AppUser;
import com.kristalball.mams.model.AuditLog;
import com.kristalball.mams.repository.AuditLogRepository;
import com.kristalball.mams.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// This filter runs on every request.
// 1. It reads the token from the Authorization header and finds the user.
// 2. After the request is finished it saves an API log for POST/PUT/PATCH/DELETE calls.
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    public JwtFilter(JwtService jwtService, UserRepository userRepository, AuditLogRepository auditLogRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String username = "anonymous";

        // token comes like this -> "Bearer eyJhbGci..."
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                String usernameFromToken = jwtService.getUsernameFromToken(token);
                AppUser user = userRepository.findByUsername(usernameFromToken).orElse(null);

                if (user != null) {
                    username = user.getUsername();
                    List<SimpleGrantedAuthority> authorities =
                            List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(user, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // token is wrong or expired, so we treat the user as not logged in
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            saveApiLog(request, response, username);
        }
    }

    // Saves a log row for successful calls that change data.
    // GET, OPTIONS and login calls are not logged.
    private void saveApiLog(HttpServletRequest request, HttpServletResponse response, String username) {
        String method = request.getMethod();
        boolean changesData = !method.equals("GET") && !method.equals("OPTIONS");
        boolean isLoginUrl = request.getRequestURI().startsWith("/api/auth");
        boolean wasSuccessful = response.getStatus() < 400;

        if (changesData && !isLoginUrl && wasSuccessful) {
            AuditLog log = new AuditLog();
            log.setUsername(username);
            log.setMethod(method);
            log.setPath(request.getRequestURI());
            log.setStatus(response.getStatus());
            auditLogRepository.save(log);
        }
    }
}
