package org.churchcrm.churchcrmapi.identity.internal;

import org.churchcrm.churchcrmapi.identity.CreateUserDto;
import org.churchcrm.churchcrmapi.identity.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private IUserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void create_ShouldReturn201_WhenUserCreatedSuccessfully() {
        // Given
        UUID organizationId = UUID.randomUUID();
        CreateUserDto dto = new CreateUserDto("testuser", "Test User", "test@example.com");
        UserDto expectedDto = new UserDto(
            UUID.randomUUID(), "testuser", "Test User", "test@example.com", organizationId,
            null, null, null, null
        );
        
        when(userService.createUser(dto, organizationId, false)).thenReturn(expectedDto);

        // When
        ResponseEntity<UserDto> response = userController.create(dto, organizationId);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
        verify(userService).createUser(dto, organizationId, false);
    }

    @Test
    void get_ShouldReturn200_WhenUserExistsAndBelongsToOrganization() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UserDto expectedDto = new UserDto(
            userId, "testuser", "Test User", "test@example.com", organizationId,
            null, null, null, null
        );
        
        when(userService.getById(userId, organizationId)).thenReturn(expectedDto);

        // When
        ResponseEntity<UserDto> response = userController.get(userId, organizationId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
        verify(userService).getById(userId, organizationId);
    }
}