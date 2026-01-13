//package org.churchcrm.churchcrmapi.organization.internal;
//
//import org.churchcrm.churchcrmapi.organization.AddressDto;
//import org.churchcrm.churchcrmapi.organization.ChurchDto;
//import org.churchcrm.churchcrmapi.organization.CreateChurchDto;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class OrganizationControllerTest {
//
//    @Mock
//    private ChurchService churchService;
//
//    @Test
//    void createChurch_WithValidData_ShouldReturnCreatedResponse() {
//        // Given
//        var controller = new OrganizationController(churchService);
//        var dto = new CreateChurchDto("Test Church", "test.church", "user", "email@test.com", "John Doe", null);
//        var churchId = UUID.randomUUID();
//        var churchDto = new ChurchDto(churchId, "Test Church", "test.church", null, null, null, null, null);
//
//        when(churchService.createChurch(dto)).thenReturn(churchDto);
//
//        // When
//        ResponseEntity<Void> response = controller.createChurch(dto);
//
//        // Then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//        assertThat(response.getHeaders().getLocation().toString()).isEqualTo("/churches/" + churchId);
//    }
//
//    @Test
//    void createChurch_WithAddress_ShouldPassAddressToService() {
//        // Given
//        var controller = new OrganizationController(churchService);
//        var address = new AddressDto("123 Main St", null, "City", "12345");
//        var dto = new CreateChurchDto("Test Church", "test.church", "user", "email@test.com", "John Doe", address);
//        var churchId = UUID.randomUUID();
//        var churchDto = new ChurchDto(churchId, "Test Church", "test.church", null, null, null, null, null);
//
//        when(churchService.createChurch(dto)).thenReturn(churchDto);
//
//        // When
//        ResponseEntity<Void> response = controller.createChurch(dto);
//
//        // Then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//        assertThat(response.getHeaders().getLocation().toString()).isEqualTo("/churches/" + churchId);
//    }
//}