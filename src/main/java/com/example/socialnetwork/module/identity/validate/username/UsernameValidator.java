package com.example.socialnetwork.module.identity.validate.username;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class UsernameValidator implements ConstraintValidator<UserNameConstraint, String> {

    @Value("${validation.user.username-min:3}")
    int min;

    @Value("${validation.user.username-max:16}")
    int max;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return value.length() >= min && value.length() <= max;
    }
}