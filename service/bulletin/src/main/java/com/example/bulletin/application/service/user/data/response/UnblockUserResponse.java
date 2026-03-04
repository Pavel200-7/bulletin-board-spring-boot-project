package com.example.bulletin.application.service.user.data.response;

import com.example.bulletin.application.data.response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnblockUserResponse {
    private UserResponse userResponse;
}
