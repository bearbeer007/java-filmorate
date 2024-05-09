package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder(toBuilder = true)
public class User {

    private Long id;
    @Email(message = "Email is incorrect")
    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "Login is required")
    @Pattern(regexp = "\\S+", message = "Login must not contain space characters")
    private String login;
    private String name;
    @NotNull(message = "Birthday is required")
    @PastOrPresent(message = "Birthday must not be later than the current date")
    private LocalDate birthday;

    private final Set<Long> friends = new HashSet<>();

    private final Set<Long> likedFilms = new HashSet<>();

    public String getName() {
        if (this.name == null || this.name.isBlank()) {
            this.name = this.getLogin();
        }
        return this.name;
    }
}