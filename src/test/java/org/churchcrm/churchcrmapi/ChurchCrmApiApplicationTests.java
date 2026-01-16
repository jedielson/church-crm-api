package org.churchcrm.churchcrmapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

@SpringBootTest
class ChurchCrmApiApplicationTests {
    
    private final ApplicationModules modules = ApplicationModules.of(ChurchCrmApiApplication.class);

    @Test
    void moduleTests() {
        modules.verify();
        System.out.println(modules);
    }

    @Test
    void writeDocumentation() {
        new Documenter(modules)
                .writeModulesAsPlantUml()
                .writeIndividualModulesAsPlantUml()
                .writeDocumentation();
    }
}
