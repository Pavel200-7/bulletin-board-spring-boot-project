package com.example.auth.host.controller.data;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String refreshToken;
}