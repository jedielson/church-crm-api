package org.churchcrm.churchcrmapi.arch.rules;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import jakarta.persistence.EntityListeners;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

public class AuditingEntityListenerRule extends ArchCondition<JavaClass> {

    public AuditingEntityListenerRule(Object... args) {
        super("have @EntityListeners(AuditingEntityListener.class) annotation", args);
    }

    @Override
    public void check(JavaClass javaClass, ConditionEvents events) {
        boolean hasEntityListeners = javaClass.isAnnotatedWith(EntityListeners.class);

        if (!hasEntityListeners) {
            String message = String.format(
                    "Class %s does not have @EntityListeners(AuditingEntityListener.class) annotation. " +
                            "This is required for JPA auditing to work properly.",
                    javaClass.getName()
            );
            events.add(SimpleConditionEvent.violated(javaClass, message));
            return;
        }

        // Verify it's specifically AuditingEntityListener
        EntityListeners annotation = javaClass.getAnnotationOfType(EntityListeners.class);
        Class<?>[] listeners = annotation.value();

        boolean hasAuditingEntityListener = false;
        for (Class<?> listener : listeners) {
            if (listener.equals(AuditingEntityListener.class)) {
                hasAuditingEntityListener = true;
                break;
            }
        }

        if (!hasAuditingEntityListener) {
            String message = String.format(
                    "Class %s has @EntityListeners but not with AuditingEntityListener.class. " +
                            "Required: @EntityListeners(AuditingEntityListener.class)",
                    javaClass.getName()
            );
            events.add(SimpleConditionEvent.violated(javaClass, message));
        }
    }
}
