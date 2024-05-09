package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


import java.time.LocalDate;

public class MovieBirthdayValidator implements ConstraintValidator<MovieBirthday, LocalDate> {
    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (localDate == null) return false;
        return localDate.isAfter(LocalDate.of(1895, 12, 27));
    }
}
