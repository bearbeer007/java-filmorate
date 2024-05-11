package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = MovieBirthdayValidator.class)
public @interface MovieBirthday {

    public String message() default "The date being checked is older than the movie's birthday 28th DEC 1895.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
