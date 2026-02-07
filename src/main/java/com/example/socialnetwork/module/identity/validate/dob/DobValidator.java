package com.example.socialnetwork.module.identity.validate.dob;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.Period;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DobValidator implements ConstraintValidator<DobConstraint, LocalDate> {
    int min;
    int max;

    @Override
    public void initialize(DobConstraint constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        LocalDate today = LocalDate.now();
        int age = Period.between(value, today).getYears();
        log.info("Age: {}", age);

        return age >= min && age <= max;
    }
}
