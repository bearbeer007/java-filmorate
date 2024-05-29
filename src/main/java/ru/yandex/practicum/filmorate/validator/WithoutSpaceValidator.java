package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.validator.interfaces.WithoutSpace;



public class WithoutSpaceValidator implements ConstraintValidator<WithoutSpace, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.chars().noneMatch(Character::isWhitespace);
    }
}
