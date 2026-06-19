package com.hirsphere.backend.service;

import com.hirsphere.backend.dto.AuthResponse;
import com.hirsphere.backend.dto.LoginRequest;
import com.hirsphere.backend.dto.RegisterRequest;

public interface AuthService {

    void register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

}