package com.example.bulletin.unit.application.service.user.service;

import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.UserMapper;
import com.example.bulletin.application.service.user.UserServiceImpl;
import com.example.bulletin.application.service.user.data.request.GetUserRequest;
import com.example.bulletin.application.service.user.data.response.GetUserResponse;
import com.example.bulletin.application.service.user.data.response.data.UserResponse;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GetUserTests {

    @Autowired
    private UserMapper mapperHelper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserServiceImpl service;


    @BeforeEach
    public void setup() {
        User user = createUser();
        when(userRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(user));

        when(mapper.toResponse(any(User.class)))
                .thenAnswer(invocation ->
                {
                    User userArg = invocation.getArgument(0);
                    return mapperHelper.toResponse(userArg);
                });
    }

    @Test
    public void shouldThrowWhenUserNotFound() {
        // Arrange
        GetUserRequest request = createRequest();
        when(userRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.getUser(request));
    }

    @Test
    public void shouldReturnUserWhenFound() {
        // Arrange
        GetUserRequest request = createRequest();
        UserResponse expected = UserResponse.builder()
                .id(request.getId())
                .email(createEmail())
                .blocked(false)
                .build();

        // Act
        GetUserResponse response = service.getUser(request);
        UserResponse actual = response.getUserResponse();

        // Assert
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    public GetUserRequest createRequest() {
        return GetUserRequest.builder()
                .id(UUID.randomUUID())
                .build();
    }

    public User createUser() {
        String email = createEmail();
        return User.createUser(UUID.randomUUID(), email);
    }

    public String createEmail() {
        return "test@example.com";
    }

}