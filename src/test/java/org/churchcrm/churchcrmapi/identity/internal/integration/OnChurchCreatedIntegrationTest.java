package org.churchcrm.churchcrmapi.identity.internal.integration;

import net.datafaker.Faker;
import org.churchcrm.churchcrmapi.identity.internal.IUserService;
import org.churchcrm.churchcrmapi.organization.ChurchCreated;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.churchcrm.churchcrmapi.utils.TestAuditingConfig;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ApplicationModuleTest
@Import(TestAuditingConfig.class)
class OnChurchCreatedIntegrationTest {

    @Autowired
    private IUserService userService;

    @Test
    @WithMockUser(username = "user")
    void shouldCreateUserWhenChurchCreatedEventArrives(Scenario scenario) {
        UUID churchId = UUID.randomUUID();
        var faker = new Faker();
        var username = faker.internet().emailAddress();
        var fullName = faker.name().fullName();

        var event = new ChurchCreated(
                churchId,
                fullName,
                username,
                username,
                fullName);

        scenario.publish(event)
                .andWaitForStateChange(() -> userService.getByEmail(event.getUserName()))
                .andVerify(user -> assertThat(user).isNotNull());
    }
}