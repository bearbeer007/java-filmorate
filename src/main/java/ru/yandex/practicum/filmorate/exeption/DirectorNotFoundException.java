package ru.yandex.practicum.filmorate.exeption;

public class DirectorNotFoundException extends RuntimeException{
    public DirectorNotFoundException(final String message) {
        super(message);
    }
}
