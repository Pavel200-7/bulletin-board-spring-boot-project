package com.example.auth.host.controller.data;

import lombok.Data;

@Data
public class LogoutRequest {
    private String refreshToken;
}