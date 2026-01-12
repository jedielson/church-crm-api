package org.churchcrm.churchcrmapi.arch.rules;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ArchUnit rule to enforce that all controller methods return ResponseEntity.
 * 
 * <p>This ensures consistent response handling across all REST endpoints with:</p>
 * <ul>
 *   <li>Explicit HTTP status codes</li>
 *   <li>Custom headers when needed</li>
 *   <li>Better control over response structure</li>
 *   <li>Improved testability</li>
 * </ul>
 * 
 * <p>Example of correct usage:</p>
 * <pre>
 * {@code
 * @GetMapping("/{id}")
 * public ResponseEntity<ChurchDto> getChurch(@PathVariable UUID id) {
 *     ChurchDto church = service.getChurch(id);
 *     return ResponseEntity.ok(church);  // ✓ Correct - returns ResponseEntity
 * }
 * 
 * @PostMapping
 * public ResponseEntity<ChurchDto> createChurch(@RequestBody CreateChurchDto dto) {
 *     ChurchDto church = service.create(dto);
 *     return ResponseEntity.status(HttpStatus.CREATED).body(church);  // ✓ Correct
 * }
 * }
 * </pre>
 * 
 * <p>Example of incorrect usage that will fail this rule:</p>
 * <pre>
 * {@code
 * @GetMapping("/{id}")
 * public ChurchDto getChurch(@PathVariable UUID id) {  // ✗ Wrong - returns ChurchDto directly
 *     return service.getChurch(id);
 * }
 * 
 * @PostMapping
 * public void createChurch(@RequestBody CreateChurchDto dto) {  // ✗ Wrong - returns void
 *     service.create(dto);
 * }
 * }
 * </pre>
 */
public class ControllerResponseRule extends ArchCondition<JavaClass> {

    public ControllerResponseRule() {
        super("have all public methods return ResponseEntity");
    }

    @Override
    public void check(JavaClass javaClass, ConditionEvents events) {
        // Only check classes that are controllers
        if (!isController(javaClass)) {
            return;
        }

        // Check all public methods
        for (JavaMethod method : javaClass.getMethods()) {
            // Only check methods that have REST mapping annotations
            if (hasRestMappingAnnotation(method) && !returnsResponseEntity(method)) {
                String message = String.format(
                        "Method '%s.%s()' is a REST endpoint but returns '%s' instead of ResponseEntity. " +
                        "All controller methods should return ResponseEntity for consistent response handling.",
                        javaClass.getSimpleName(),
                        method.getName(),
                        method.getRawReturnType().getSimpleName()
                );
                events.add(SimpleConditionEvent.violated(javaClass, message));
            }
        }
    }

    /**
     * Checks if the class is a Spring controller.
     */
    private boolean isController(JavaClass javaClass) {
        return javaClass.isAnnotatedWith(RestController.class) ||
               javaClass.isAnnotatedWith(org.springframework.stereotype.Controller.class);
    }

    /**
     * Checks if the method has a REST mapping annotation.
     */
    private boolean hasRestMappingAnnotation(JavaMethod method) {
        return method.isAnnotatedWith(GetMapping.class) ||
               method.isAnnotatedWith(PostMapping.class) ||
               method.isAnnotatedWith(PutMapping.class) ||
               method.isAnnotatedWith(PatchMapping.class) ||
               method.isAnnotatedWith(DeleteMapping.class) ||
               method.isAnnotatedWith(RequestMapping.class);
    }

    /**
     * Checks if the method returns ResponseEntity.
     */
    private boolean returnsResponseEntity(JavaMethod method) {
        return method.getRawReturnType().isEquivalentTo(ResponseEntity.class);
    }
}
