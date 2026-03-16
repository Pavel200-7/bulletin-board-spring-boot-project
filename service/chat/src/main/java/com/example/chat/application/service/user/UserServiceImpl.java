package com.example.chat.application.service.user;

import com.example.chat.application.data.response.UserResponse;
import com.example.chat.application.exception.DuplicateResourceException;
import com.example.chat.application.mapper.UserMapper;
import com.example.chat.application.service.user.data.request.*;
import com.example.chat.application.service.user.data.response.*;
import com.example.chat.domain.entity.base.user.User;
import com.example.chat.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    @Transactional
    public CreateUserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User with this email is already exist");
        }

        UUID userId = request.getId() != null ? request.getId() : UUID.randomUUID();
        User user = User.createUser(userId, request.getEmail());
        userRepository.save(user);

        UserResponse userResponse = mapper.toResponse(user);
        return new CreateUserResponse(userResponse);
    }

}