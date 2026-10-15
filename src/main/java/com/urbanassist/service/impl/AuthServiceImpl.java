package com.urbanassist.service.impl;

import com.urbanassist.dto.request.LoginRequest;
import com.urbanassist.dto.request.RegisterRequest;
import com.urbanassist.dto.response.AuthResponse;
import com.urbanassist.dto.response.UserResponse;
import com.urbanassist.entity.Role;
import com.urbanassist.entity.User;
import com.urbanassist.exception.ResourceNotFoundException;
import com.urbanassist.exception.UnauthorizedException;
import com.urbanassist.repository.UserRepository;
import com.urbanassist.security.JwtTokenProvider;
import com.urbanassist.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail().toLowerCase().trim())) {
            throw new IllegalArgumentException("An account with email " + request.getEmail() + " already exists.");
        }

        User user = new User(
                request.getFullName().trim(),
                request.getEmail().toLowerCase().trim(),
                passwordEncoder.encode(request.getPassword()),
                Role.ROLE_CITIZEN
        );
        user.setPhone(request.getPhone());
        User savedUser = userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(savedUser.getEmail(), request.getPassword())
        );

        String token = tokenProvider.generateToken(authentication);
        return new AuthResponse(token, UserResponse.fromEntity(savedUser));
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().toLowerCase().trim();
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

            String token = tokenProvider.generateToken(authentication);
            return new AuthResponse(token, UserResponse.fromEntity(user));
        } catch (Exception ex) {
            throw new UnauthorizedException("Invalid email or password credentials.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return UserResponse.fromEntity(user);
    }
}
