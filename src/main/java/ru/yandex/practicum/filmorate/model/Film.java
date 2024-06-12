package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.interfaces.ValidReleaseDate;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
@Builder(toBuilder = true)
public class Film {

    private Long id;
    @NotBlank
    private String name;
    @Size(min = 0, max = 200)
    @NotNull
    private String description;
    @ValidReleaseDate
    private LocalDate releaseDate;
    @Positive
    @NotNull
    private Integer duration;
    private Set<Long> likeIds = new HashSet<>();
    private Set<Genre> genres = new HashSet<>();
    @NotNull
    private Mpa mpa;
    private Set<Director> directors;

    @Override
    public boolean equals(Object film) {
        if (film == null) return false;
        if (film.getClass() != this.getClass()) return false;
        final Film other = (Film) film;
        if (other.id == null) return false;
        return name.equals(other.name) && description.equals(other.description) &&
                releaseDate.equals(other.releaseDate);
    }
}
