package com.urbanassist.service;

import com.urbanassist.dto.request.LoginRequest;
import com.urbanassist.dto.request.RegisterRequest;
import com.urbanassist.dto.response.AuthResponse;
import com.urbanassist.dto.response.UserResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    UserResponse getCurrentUser(String email);
}
