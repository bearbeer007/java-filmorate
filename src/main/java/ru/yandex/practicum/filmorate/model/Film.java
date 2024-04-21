package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;


import java.time.LocalDate;


@Data
public class Film {

    private int id;
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Description is required")
    @Size(max = 200, message = "Description must be maximum 200 characters")
    private String description;
    @NotBlank(message = "Release date is required")
    private LocalDate releaseDate;
    @Positive(message = "Duration is required and must be greater than 0")
    private long duration;
}
