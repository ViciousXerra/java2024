package edu.java.bot.api.dto.customvalidations;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class LinkValidator implements ConstraintValidator<LinkValidator.LinkValidation, String> {

    private final static String LINK_REGEX =
        "https?://(www\\.)?([-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b)([-a-zA-Z0-9()@:%_+.~#?&/=]*)";

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s.matches(LINK_REGEX);
    }

    @Constraint(validatedBy = LinkValidator.class)
    @Target({ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface LinkValidation {

        String message() default "Mapped value must be formatted in URI pattern.";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};

    }

}
