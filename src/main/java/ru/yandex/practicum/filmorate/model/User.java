package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {

    private int id;
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
    @JsonIgnore
    private Set<Long> friendsIds = new HashSet<>();
    public String getName() {
        if (this.name == null || this.name.isBlank()) {
            this.name = this.getLogin();
        }
        return this.name;
    }
}