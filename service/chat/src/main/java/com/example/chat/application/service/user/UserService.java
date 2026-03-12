package com.example.chat.application.service.user;

import com.example.notification.application.service.user.data.request.*;
import com.example.notification.application.service.user.data.response.*;

public interface UserService {
    CreateUserResponse createUser(CreateUserRequest request);
}
