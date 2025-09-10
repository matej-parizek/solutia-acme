package cz.solutia.acme.core.validator;

import jakarta.validation.Constraint;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PasswordConstrainsValidator.class)
public @interface ValidPassword {
    String message() default "{password.invalid}";

    Class<?>[] groups() default {};

    Class<? extends jakarta.validation.Payload>[] payload() default {};
}
