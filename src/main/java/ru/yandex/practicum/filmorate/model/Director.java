package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Director {
    private Long id;
    private String name;

    @Override
    public boolean equals(Object director) {
        if (director == null) return false;
        if (director.getClass() != this.getClass()) return false;
        final Director other = (Director) director;
        if (other.id == null) return false;
        return name.equals(other.name) && id.equals(other.id);
    }
}
