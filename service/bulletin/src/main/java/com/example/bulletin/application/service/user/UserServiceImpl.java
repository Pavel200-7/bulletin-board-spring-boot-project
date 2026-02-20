package com.example.bulletin.application.service.user;

import com.example.bulletin.application.exception.DuplicateResourceException;
import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.UserMapper;
import com.example.bulletin.application.service.user.data.request.*;
import com.example.bulletin.application.service.user.data.response.*;
import com.example.bulletin.application.service.user.data.response.data.UserResponse;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.infrastructure.repository.UserRepository;
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
    public GetUserResponse getUser(GetUserRequest request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User with this id is not found."));
        UserResponse userResponse = mapper.toResponse(user);
        return new GetUserResponse(userResponse);
    }

    @Override
    @Transactional
    public CreateUserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User with this email is already exist.");
        }

        UUID userId = request.getId() != null ? request.getId() : UUID.randomUUID();
        User user = User.createUser(userId, request.getEmail());
        user = userRepository.save(user);

        UserResponse userResponse = mapper.toResponse(user);
        return new CreateUserResponse(userResponse);
    }

    @Override
    @Transactional
    public BlockUserResponse blockUser(BlockUserRequest request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User with this id is not found."));

        user.setBlocked(true);
        user = userRepository.save(user);

        UserResponse userResponse = mapper.toResponse(user);
        return new BlockUserResponse(userResponse);
    }

    @Override
    @Transactional
    public UnblockUserResponse unblockUser(UnblockUserRequest request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User with this id is not found."));

        user.setBlocked(false);
        user = userRepository.save(user);

        UserResponse userResponse = mapper.toResponse(user);
        return new UnblockUserResponse(userResponse);
    }

}