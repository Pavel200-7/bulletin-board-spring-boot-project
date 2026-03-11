package com.example.notification.application.service.user.data.response;

import com.example.notification.application.data.response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserResponse {
    private UserResponse userResponse;
}
