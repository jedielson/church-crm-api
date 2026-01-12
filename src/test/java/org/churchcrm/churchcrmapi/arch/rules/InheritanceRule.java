package org.churchcrm.churchcrmapi.arch.rules;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

public class InheritanceRule extends ArchCondition<JavaClass> {
    public InheritanceRule(Object... args) {
        super("not use inheritance (should only extend Object)", args);
    }

    @Override
    public void check(JavaClass javaClass, ConditionEvents events) {
        // Get the superclass
        var superclass = javaClass.getRawSuperclass();

        // If superclass is not Object, it's using inheritance
        if (superclass.isPresent() && !superclass.get().isEquivalentTo(Object.class)) {
            String message = String.format(
                    "Class %s extends %s instead of directly extending Object (composition over inheritance)",
                    javaClass.getName(),
                    superclass.get().getName()
            );
            events.add(SimpleConditionEvent.violated(javaClass, message));
        }
    }
}
