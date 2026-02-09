package org.churchcrm.churchcrmapi.identity.internal;

import org.churchcrm.churchcrmapi.crosscutting.web.ConflictException;
import org.churchcrm.churchcrmapi.crosscutting.web.NotFoundException;
import org.churchcrm.churchcrmapi.identity.CreateUserDto;
import org.churchcrm.churchcrmapi.identity.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private UserMapper mapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserService userService;

    @Test
    void create_ShouldThrowConflict_WhenUsernameExistsInOrganization() {
        // Given
        UUID organizationId = UUID.randomUUID();
        CreateUserDto dto = new CreateUserDto("testuser", "Test User", "test@example.com");
        
        when(repository.existsByUsernameAndChurchId("testuser", organizationId))
            .thenReturn(true);

        // When & Then
        ConflictException exception = assertThrows(
            ConflictException.class,
            () -> userService.createUser(dto, organizationId, false)
        );
        
        assertEquals("User with this username already exists in this organization", exception.getMessage());
//        assertEquals("username", exception.getField());
//        assertEquals("testuser", exception.getFieldValue());
    }

    @Test
    void create_ShouldThrowConflict_WhenEmailExistsInOrganization() {
        // Given
        UUID organizationId = UUID.randomUUID();
        CreateUserDto dto = new CreateUserDto("testuser", "Test User", "test@example.com");
        
        when(repository.existsByUsernameAndChurchId("testuser", organizationId))
            .thenReturn(false);
        when(repository.existsByEmailAndChurchId("test@example.com", organizationId))
            .thenReturn(true);

        // When & Then
        ConflictException exception = assertThrows(
            ConflictException.class,
            () -> userService.createUser(dto, organizationId, false)
        );
        
        assertEquals("User with this email already exists in this organization", exception.getMessage());
//        assertEquals("email", exception.getField());
//        assertEquals("test@example.com", exception.getFieldValue());
    }

    @Test
    void getById_ShouldReturnUser_WhenUserExistsAndBelongsToOrganization() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setChurchId(organizationId);
        user.setUsername("testuser");
        user.setFullname("Test User");
        user.setEmail("test@example.com");
        
        UserDto expectedDto = new UserDto(
            userId, "testuser", "Test User", "test@example.com", organizationId,
            null, null, null, null
        );
        
        when(repository.findById(userId)).thenReturn(Optional.of(user));
        when(mapper.toDto(user)).thenReturn(expectedDto);

        // When
        UserDto result = userService.getById(userId, organizationId);

        // Then
        assertEquals(expectedDto, result);
        verify(repository).findById(userId);
        verify(mapper).toDto(user);
    }

    @Test
    void getById_ShouldThrowNotFound_WhenUserDoesNotExist() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        
        when(repository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(
            NotFoundException.class,
            () -> userService.getById(userId, organizationId)
        );
        
        assertEquals("User", exception.getResourceType());
        assertEquals(userId, exception.getResourceId());
    }

    @Test
    void getById_ShouldThrowNotFound_WhenUserExistsButBelongsToDifferentOrganization() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID differentOrgId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setChurchId(differentOrgId); // Different organization
        
        when(repository.findById(userId)).thenReturn(Optional.of(user));

        // When & Then
        NotFoundException exception = assertThrows(
            NotFoundException.class,
            () -> userService.getById(userId, organizationId)
        );
        
        assertEquals("User", exception.getResourceType());
        assertEquals(userId, exception.getResourceId());
    }
}