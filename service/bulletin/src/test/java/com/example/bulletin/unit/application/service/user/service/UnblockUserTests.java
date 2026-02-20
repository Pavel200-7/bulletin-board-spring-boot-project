package com.example.bulletin.unit.application.service.user.service;

import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.UserMapper;
import com.example.bulletin.application.service.user.UserServiceImpl;
import com.example.bulletin.application.service.user.data.request.UnblockUserRequest;
import com.example.bulletin.application.service.user.data.response.UnblockUserResponse;
import com.example.bulletin.application.service.user.data.response.data.UserResponse;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UnblockUserTests {

    @Autowired
    private UserMapper mapperHelper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserServiceImpl service;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @BeforeEach
    public void setup() {
        User user = createUser();
        when(userRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenAnswer(i -> i.getArgument(0));

        when(mapper.toResponse(any(User.class)))
                .thenAnswer(i -> mapperHelper.toResponse(i.getArgument(0)));
    }

    @Test
    public void shouldThrowWhenUserNotFound() {
        // Arrange
        UnblockUserRequest request = createRequest();
        when(userRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.unblockUser(request));
    }

    @Test
    public void shouldUnblockUserAndSave() {
        // Arrange
        UnblockUserRequest request = createRequest();

        // Act
        service.unblockUser(request);

        // Assert
        verify(userRepository).save(userCaptor.capture());
        User actual = userCaptor.getValue();

        assertThat(actual.isBlocked()).isFalse();
    }

    @Test
    public void shouldMapBeforeReturn() {
        // Arrange
        UnblockUserRequest request = createRequest();
        UserResponse expected = UserResponse.builder()
                .email("test@example.com")
                .blocked(false)
                .build();

        // Act
        UnblockUserResponse response = service.unblockUser(request);
        UserResponse actual = response.getUserResponse();

        // Assert
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    private UnblockUserRequest createRequest() {
        return UnblockUserRequest.builder()
                .id(UUID.randomUUID())
                .build();
    }

    private User createUser() {
        User user = User.createUser(UUID.randomUUID(), "test@example.com");
        user.setBlocked(true);
        return user;
    }

}