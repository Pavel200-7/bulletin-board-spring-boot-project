package com.example.chat.unit.application.service.user.service;

import com.example.chat.application.data.response.UserResponse;
import com.example.chat.application.exception.DuplicateResourceException;
import com.example.chat.application.mapper.UserMapper;
import com.example.chat.application.service.user.UserServiceImpl;
import com.example.chat.application.service.user.data.request.CreateUserRequest;
import com.example.chat.application.service.user.data.response.CreateUserResponse;
import com.example.chat.domain.entity.base.user.User;
import com.example.chat.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CreateUserTests {

    private UserMapper mapperHelper = Mappers.getMapper(
            UserMapper.class);

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
        when(userRepository.save(any(User.class)))
                .thenAnswer(i -> i.getArgument(0));
        when(mapper.toResponse(any(User.class)))
                .thenAnswer(i -> mapperHelper.toResponse(i.getArgument(0)));
    }

    @Test
    public void shouldThrowWhenEmailAlreadyExists() {
        // Arrange
        CreateUserRequest request = createRequest();
        when(userRepository.existsByEmail(any(String.class))).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> service.createUser(request));
    }

    @Test
    public void shouldCreateUserAndSave() {
        // Arrange
        CreateUserRequest request = createRequest();
        when(userRepository.existsByEmail(any(String.class)))
                .thenReturn(false);
        User expected = User.createUser(null, "test@example.com");

        // Act
        service.createUser(request);

        // Assert
        verify(userRepository).save(userCaptor.capture());
        User actual = userCaptor.getValue();

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @Test
    public void shouldMapBeforeReturn() {
        // Arrange
        CreateUserRequest request = createRequest();
        when(userRepository.existsByEmail(any(String.class)))
                .thenReturn(false);
        UserResponse expected = UserResponse.builder()
                .email("test@example.com")
                .blocked(false)
                .build();

        // Act
        CreateUserResponse response = service.createUser(request);
        UserResponse actual = response.getUserResponse();

        // Assert
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    private CreateUserRequest createRequest() {
        return CreateUserRequest.builder()
                .email("test@example.com")
                .build();
    }

}