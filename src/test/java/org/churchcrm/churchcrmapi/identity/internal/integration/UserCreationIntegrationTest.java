package org.churchcrm.churchcrmapi.identity.internal.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.churchcrm.churchcrmapi.identity.CreateUserDto;
import org.churchcrm.churchcrmapi.identity.UserDto;
import org.churchcrm.churchcrmapi.utils.JwtTestDsl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.security.enabled=true"
})
@Transactional
class UserCreationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void createUser_WithValidData_ShouldCreateUser() throws Exception {
        // Given
        var faker = new Faker();
        var username = faker.internet().emailAddress();
        var fullName = faker.name().fullName();
        var dto = new CreateUserDto(username, fullName, username);
        UUID organizationId = UUID.randomUUID();

        // When/Then
        var response = mockMvc.perform(post("/users")
                        .with(JwtTestDsl.jwt()
                                .organization(organizationId)
                                .admin()
                                .build())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();

        assertThat(response.getResponse().getStatus()).isEqualTo(201);

        String responseBody = response.getResponse().getContentAsString();
        UserDto createdUser = objectMapper.readValue(responseBody, UserDto.class);
        String location = response.getResponse().getHeader("Location");

        assertThat(createdUser.username()).isEqualTo(username);
        assertThat(createdUser.fullname()).isEqualTo(fullName);
        assertThat(createdUser.email()).isEqualTo(username);
        assertThat(createdUser.churchId()).isEqualTo(organizationId);
        assertThat(location).endsWith("/users/" + createdUser.id());
    }

    @Test
    void createUser_WithDuplicateUsername_ShouldReturnConflict() throws Exception {
        // Given
        var faker = new Faker();
        var username = faker.internet().emailAddress();
        var dto1 = new CreateUserDto(username, faker.name().fullName(), username);
        var dto2 = new CreateUserDto(username, faker.name().fullName(), username);
        UUID organizationId = UUID.randomUUID();

        // Create first user
        mockMvc.perform(post("/users")
                        .with(JwtTestDsl.jwt()
                                .organization(organizationId)
                                .admin()
                                .build())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto1)))
                .andExpect(status().isCreated());

        // When/Then - try to create second user with same username
        mockMvc.perform(post("/users")
                        .with(JwtTestDsl.jwt()
                                .organization(organizationId)
                                .admin()
                                .build())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto2)))
                .andExpect(status().isConflict())
                .andExpect(header().doesNotExist("Location"));
    }

    @Test
    void createUser_WithDuplicateEmail_ShouldReturnConflict() throws Exception {
        // Given
        var faker = new Faker();
        var username = faker.internet().emailAddress();
        var dto1 = new CreateUserDto(username, faker.name().fullName(), username);
        var dto2 = new CreateUserDto(username, faker.name().fullName(), username);
        UUID organizationId = UUID.randomUUID();

        // Create first user
        mockMvc.perform(post("/users")
                        .with(JwtTestDsl.jwt()
                                .organization(organizationId)
                                .admin()
                                .build())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto1)))
                .andExpect(status().isCreated());

        // When/Then - try to create second user with same email
        mockMvc.perform(post("/users")
                        .with(JwtTestDsl.jwt()
                                .organization(organizationId)
                                .admin()
                                .build())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto2)))
                .andExpect(status().isConflict())
                .andExpect(header().doesNotExist("Location"));
    }

    @Test
    void createUser_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given - invalid data (empty username)
        var dto = new CreateUserDto("", "Test User", "test@example.com");
        UUID organizationId = UUID.randomUUID();

        // When/Then
        mockMvc.perform(post("/users")
                        .with(JwtTestDsl.jwt()
                                .organization(organizationId)
                                .admin()
                                .build())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("Location"));
    }

    @Test
    void getUser_WithValidId_ShouldReturnUser() throws Exception {
        // Given
        var faker = new Faker();
        var username = faker.internet().emailAddress();
        var fullname = faker.name().fullName();
        var createDto = new CreateUserDto(username, fullname, username);
        UUID organizationId = UUID.randomUUID();

        // Create a user first
        var createResponse = mockMvc.perform(post("/users")
                        .with(JwtTestDsl.jwt()
                                .organization(organizationId)
                                .admin()
                                .build())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = createResponse.getResponse().getContentAsString();
        UserDto createdUser = objectMapper.readValue(responseBody, UserDto.class);

        // When/Then - get the created user
        mockMvc.perform(get("/users/" + createdUser.id())
                        .with(JwtTestDsl.jwt()
                                .organization(organizationId)
                                .admin()
                                .build()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.fullname").value(fullname))
                .andExpect(jsonPath("$.email").value(username));
    }

    @Test
    void getUser_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        UUID invalidId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();

        // When/Then
        mockMvc.perform(get("/users/" + invalidId)
                        .with(JwtTestDsl.jwt()
                                .organization(organizationId)
                                .admin()
                                .build()))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_WithShortUsername_ShouldReturnBadRequest() throws Exception {
        // Given - username too short (min 3)
        var dto = new CreateUserDto("ab", "Test User", "test@example.com");
        UUID organizationId = UUID.randomUUID();

        // When/Then
        mockMvc.perform(post("/users")
                        .with(JwtTestDsl.jwt()
                                .organization(organizationId)
                                .admin()
                                .build())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_WithLongUsername_ShouldReturnBadRequest() throws Exception {
        // Given - username too long (max 50)
        var longUsername = "a".repeat(51); // 51 characters
        var dto = new CreateUserDto(longUsername, "Test User", "test@example.com");
        UUID organizationId = UUID.randomUUID();

        // When/Then
        mockMvc.perform(post("/users")
                        .with(JwtTestDsl.jwt()
                                .organization(organizationId)
                                .admin()
                                .build())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_WithLongFullname_ShouldReturnBadRequest() throws Exception {
        // Given - fullname too long (max 200)
        var longFullname = "a".repeat(201); // 201 characters
        var dto = new CreateUserDto("testuser", longFullname, "test@example.com");
        UUID organizationId = UUID.randomUUID();

        // When/Then
        mockMvc.perform(post("/users")
                        .with(JwtTestDsl.jwt()
                                .organization(organizationId)
                                .admin()
                                .build())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {
        // Given - invalid email
        var dto = new CreateUserDto("testuser", "Test User", "invalid-email");
        UUID organizationId = UUID.randomUUID();

        // When/Then
        mockMvc.perform(post("/users")
                        .with(JwtTestDsl.jwt()
                                .organization(organizationId)
                                .admin()
                                .build())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_WithTooLongEmail_ShouldReturnBadRequest() throws Exception {
        // Given - email too long (max 254)
        var longEmail = "a".repeat(250) + "@example.com"; // 264 characters
        var dto = new CreateUserDto("testuser", "Test User", longEmail);
        UUID organizationId = UUID.randomUUID();

        // When/Then
        mockMvc.perform(post("/users")
                        .with(JwtTestDsl.jwt()
                                .organization(organizationId)
                                .admin()
                                .build())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}