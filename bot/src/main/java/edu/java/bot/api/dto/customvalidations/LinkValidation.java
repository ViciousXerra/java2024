package edu.java.bot.api.dto.customvalidations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = LinkValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LinkValidation {

    String message() default "Mapped value must be formatted in URI pattern.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
