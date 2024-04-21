package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {

    private int id;
    @Email(message = "Email is incorrect")
    @NotNull(message = "Email is required")
    private String email;
    @NotBlank(message = "Login is required")
    @Pattern(regexp = "\\S+", message = "Login must not contain space characters")
    private String login;
    private String name;
    @NotNull(message = "Birthday is required")
    @PastOrPresent(message = "Birthday must not be later than the current date")
    private LocalDate birthday;

    public String getName() {
        if (this.name == null || this.name.isBlank()) {
            this.name = this.getLogin();
        }
        return this.name;
    }
}
