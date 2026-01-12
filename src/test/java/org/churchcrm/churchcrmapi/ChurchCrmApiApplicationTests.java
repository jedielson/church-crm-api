package org.churchcrm.churchcrmapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;

@SpringBootTest
class ChurchCrmApiApplicationTests {

    @Test
    void moduleTests() {
        var modules = ApplicationModules.of(ChurchCrmApiApplication.class);
        modules.verify();
        System.out.println(modules);
    }
}
