package org.churchcrm.churchcrmapi.organization.internal.integration;

import net.datafaker.Faker;
import org.churchcrm.churchcrmapi.organization.ChurchCreated;
import org.churchcrm.churchcrmapi.utils.TestAuditingConfig;
import org.churchcrm.churchcrmapi.organization.CreateChurchDto;
import org.churchcrm.churchcrmapi.organization.internal.IChurchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.security.test.context.support.WithMockUser;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ApplicationModuleTest
@Import(TestAuditingConfig.class)
class ChurchCreatedEventTest {

    @Autowired
    private IChurchService churchService;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldRaiseEventWhenChurchCreated(Scenario scenario) {
        var faker = new Faker();
        var churchName = faker.company().name();
        var hostName = faker.internet().domainName();
        var userName = faker.internet().emailAddress();
        var fullName = faker.name().fullName();

        var dto = new CreateChurchDto(
                churchName,
                hostName,
                userName,
                userName,
                fullName,
                null
        );

        scenario.stimulate(() -> churchService.createChurch(dto))
                .andWaitForEventOfType(ChurchCreated.class)
                .matching(event -> event.getName().equals(churchName))
                .toArriveAndVerify(event -> {
                    assertThat(event.getChurchId()).isNotNull();
                    assertThat(event.getName()).isEqualTo(churchName);
                    assertThat(event.getUserName()).isEqualTo(userName);
                    assertThat(event.getEmail()).isEqualTo(userName);
                    assertThat(event.getFullName()).isEqualTo(fullName);
                });
    }
}
