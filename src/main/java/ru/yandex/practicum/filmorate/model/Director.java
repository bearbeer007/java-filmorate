package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.yandex.practicum.filmorate.validator.interfaces.Marker;

@Data
@Builder
public class Director {
    @NotNull(groups = Marker.onUpdate.class)
    private Long id;
    @NotBlank
    private String name;

}
