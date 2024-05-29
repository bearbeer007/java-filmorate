package ru.yandex.practicum.filmorate.validator.interfaces;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.yandex.practicum.filmorate.validator.WithoutSpaceValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = {WithoutSpaceValidator.class})
public @interface WithoutSpace {
    String message() default "Пробелы использовать нельзя";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
