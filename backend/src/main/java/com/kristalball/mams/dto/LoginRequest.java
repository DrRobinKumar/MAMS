package com.kristalball.mams.dto;

import lombok.Data;

// Data we get from the login form
@Data
public class LoginRequest {
    private String username;
    private String password;
}
