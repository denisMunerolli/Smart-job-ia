package com.smartjobai.api.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        long expiresIn
) {}
