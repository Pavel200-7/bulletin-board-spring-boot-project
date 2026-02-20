package com.example.bulletin.application.service.user.data.response;

import com.example.bulletin.application.service.user.data.response.data.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetUserResponse {
    private UserResponse userResponse;
}
