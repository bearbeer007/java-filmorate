package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.MovieBirthday;


import java.time.LocalDate;
import java.util.Set;


@Data
@Builder(toBuilder = true)
public class Film {

    private Long id;
    @NotBlank(message = "Name is required")
    private String name;
    @Size(max = 200, message = "Description must be maximum 200 characters")
    private String description;
    @NotNull(message = "Release date is required")
    @MovieBirthday
    private LocalDate releaseDate;
    @Positive(message = "Duration is required and must be greater than 0")
    private long duration;
    @JsonIgnore
    private Set<Long> usersLikes;
    @JsonIgnore
    private int likes = 0;
}
