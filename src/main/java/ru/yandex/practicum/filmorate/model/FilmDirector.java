package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilmDirector {
    private Long filmId;
    private Long directorId;
}
