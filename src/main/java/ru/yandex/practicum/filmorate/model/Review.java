package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class Review {
    @JsonProperty("reviewId")
    private Long id;
    @NotNull
    private Long filmId;
    @NotNull
    private Long userId;
    @NotBlank(message = "Текст отзыва не может быть пустым")
    private String content;
    @JsonProperty("isPositive")
    @NotNull
    private Boolean isPositive;

    @JsonProperty("useful")
    private int rating;
}
