package org.churchcrm.churchcrmapi.organization.internal;

import org.churchcrm.churchcrmapi.organization.AddressDto;
import org.churchcrm.churchcrmapi.organization.CreateChurchDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ChurchTest {


    private final ChurchMapper churchMapper = new ChurchMapperImpl();

    @Test
    void create_WithAddress_ShouldCreateChurchWithCongregation() {
        // Given
        var dto = new CreateChurchDto("Test Church", "test.church", "user", "email@test.com", "John Doe",
            new AddressDto("123 Main St", null, "City", "12345"));
        var church = new Church();
        church.setName("Test Church");
        var address = new Address();

        // When
        var result = Church.create(dto, churchMapper);

        // Then
        assertThat(result.getName()).isEqualTo("Test Church");
        assertThat(result.getCongregations()).hasSize(1);
        var congregation = result.getCongregations().get(0);
        assertThat(congregation.getName()).isEqualTo("Test Church"); // Should inherit church name
        assertThat(congregation.getIsMain()).isTrue();
//        assertThat(congregation.getAddress()).isEqualTo(address);
    }

    @Test
    void create_WithoutAddress_ShouldCreateChurchWithCongregationWithoutAddress() {
        // Given
        var dto = new CreateChurchDto("Test Church", "test.church", "user", "email@test.com", "John Doe", null);
        var church = new Church();
        church.setName("Test Church");

        // When
        var result = Church.create(dto, churchMapper);

        // Then
        assertThat(result.getName()).isEqualTo("Test Church");
        assertThat(result.getCongregations()).hasSize(1);
        var congregation = result.getCongregations().get(0);
        assertThat(congregation.getName()).isEqualTo("Test Church");
        assertThat(congregation.getIsMain()).isTrue();
        assertThat(congregation.getAddress()).isNull();
    }
}