package org.churchcrm.churchcrmapi.organization.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.churchcrm.churchcrmapi.organization.AddressDto;
import org.churchcrm.churchcrmapi.organization.ChurchDto;
import org.churchcrm.churchcrmapi.organization.CreateChurchDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class OrganizationControllerTest {

    @Mock
    private ChurchService churchService;

    @InjectMocks
    private OrganizationController controller;

    @Test
    void createChurch_ShouldReturn201_WhenChurchCreatedSuccessfully() {
        // Given
        var dto = new CreateChurchDto(
                "Test Church",
                "test.church",
                "user",
                "email@test.com",
                "John Doe",
                null);

        var churchId = UUID.randomUUID();
        var expectedDto = new ChurchDto(
                churchId,
                "Test Church",
                "test.church",
                null,
                null,
                null,
                null,
                null);

        when(churchService.createChurch(dto)).thenReturn(expectedDto);

        // When
        ResponseEntity<ChurchDto> response = controller.createChurch(dto);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
        verify(churchService).createChurch(dto);
    }

    @Test
    void createChurch_WithAddress_ShouldReturn201_WhenChurchCreatedSuccessfully() {
        // Given
        var address = new AddressDto("123 Main St", null, "City", "12345");
        var dto = new CreateChurchDto(
                "Test Church",
                "test.church",
                "user",
                "email@test.com",
                "John Doe",
                address);
        var churchId = UUID.randomUUID();
        var expectedDto = new ChurchDto(churchId,
                "Test Church",
                "test.church",
                null,
                null,
                null,
                null,
                null);

        when(churchService.createChurch(dto)).thenReturn(expectedDto);

        // When
        ResponseEntity<ChurchDto> response = controller.createChurch(dto);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
        verify(churchService).createChurch(dto);
    }

    @Test
    void getChurch_ShouldReturn200_WhenChurchExistsAndBelongsToOrganization() {
        // Given
        UUID churchId = UUID.randomUUID();
        UUID organizationId = churchId; // In this case, church ID is the organization ID
        var expectedDto = new ChurchDto(churchId,
                "Test Church",
                "test.church",
                null,
                null,
                null,
                null,
                null);

        when(churchService.getChurchById(churchId, organizationId)).thenReturn(expectedDto);

        // When
        ResponseEntity<ChurchDto> response = controller.getChurch(churchId, organizationId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
        verify(churchService).getChurchById(churchId, organizationId);
    }
}
