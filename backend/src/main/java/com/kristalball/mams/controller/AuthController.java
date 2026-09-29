package com.kristalball.mams.controller;

import com.kristalball.mams.dto.LoginRequest;
import com.kristalball.mams.model.AppUser;
import com.kristalball.mams.repository.UserRepository;
import com.kristalball.mams.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

// Login API
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest request) {
        // find the user and check the password
        AppUser user = userRepository.findByUsername(request.getUsername())
                .filter(u -> passwordEncoder.matches(request.getPassword(), u.getPassword()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwtService.createToken(user.getUsername()));
        response.put("username", user.getUsername());
        response.put("role", user.getRole());
        // admin does not have a base so we send empty string
        response.put("base", user.getBase() == null ? "" : user.getBase());
        return response;
    }
}
