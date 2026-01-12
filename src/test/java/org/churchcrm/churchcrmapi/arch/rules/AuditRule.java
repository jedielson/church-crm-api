package org.churchcrm.churchcrmapi.arch.rules;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import jakarta.persistence.Embedded;
import org.churchcrm.churchcrmapi.crosscutting.auditing.Audit;

public class AuditRule extends ArchCondition<JavaClass> {

    public AuditRule(Object... args) {
        super("have an @Embedded field of type Audit", args);
    }

    @Override
    public void check(JavaClass javaClass, ConditionEvents events) {
        boolean hasAuditField = javaClass.getAllFields().stream()
                .anyMatch(field ->
                        field.isAnnotatedWith(Embedded.class) &&
                                field.getRawType().isEquivalentTo(Audit.class)
                );

        if (!hasAuditField) {
            String message = String.format(
                    "Class %s does not have an @Embedded field of type Audit",
                    javaClass.getName()
            );
            events.add(SimpleConditionEvent.violated(javaClass, message));
        }
    }
}
