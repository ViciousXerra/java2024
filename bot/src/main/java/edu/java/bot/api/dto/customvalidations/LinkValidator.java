package edu.java.bot.api.dto.customvalidations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LinkValidator implements ConstraintValidator<LinkValidation, String> {

    private final static String LINK_REGEX =
        "https?://(www\\.)?([-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b)([-a-zA-Z0-9()@:%_+.~#?&/=]*)";

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s.matches(LINK_REGEX);
    }

}
