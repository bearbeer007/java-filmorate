package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
@Builder(toBuilder = true)
public class Film {

    private int id;
    @NotBlank(message = "Name is required")
    private String name;
    @Size(max = 200, message = "Description must be maximum 200 characters")
    private String description;
    @NotNull(message = "Release date is required")
    private LocalDate releaseDate;
    @Positive(message = "Duration is required and must be greater than 0")
    private long duration;

    private Set<Long> usersLikes;

    private int likes = 0;
}
