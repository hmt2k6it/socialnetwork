package com.example.socialnetwork.module.identity.validate.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class PasswordValidator implements ConstraintValidator<PasswordConstraint, String> {

    @Value("${validation.password-regex}")
    String regex;

    int min;

    int max;

    @Override
    public void initialize(PasswordConstraint constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value.length() >= min && value.length() <= max && value.matches(regex);
    }
}