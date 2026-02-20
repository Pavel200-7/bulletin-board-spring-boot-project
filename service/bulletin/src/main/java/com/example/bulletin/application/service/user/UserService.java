package com.example.bulletin.application.service.user;

import com.example.bulletin.application.service.user.data.request.*;
import com.example.bulletin.application.service.user.data.response.*;

public interface UserService {
    GetUserResponse getUser(GetUserRequest request);
    CreateUserResponse createUser(CreateUserRequest request);
    BlockUserResponse blockUser(BlockUserRequest request);
    UnblockUserResponse unblockUser(UnblockUserRequest request);
}
