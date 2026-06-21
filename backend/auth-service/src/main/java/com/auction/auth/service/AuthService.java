package com.auction.auth.service;

import com.auction.auth.dto.AuthResponse;
import com.auction.auth.dto.LoginRequest;
import com.auction.auth.dto.RefreshRequest;
import com.auction.auth.dto.RegisterRequest;

import java.util.UUID;

public interface AuthService {

    UUID register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(RefreshRequest request);
}
