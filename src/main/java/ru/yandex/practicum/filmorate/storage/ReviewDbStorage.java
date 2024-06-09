package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.interfaces.ReviewStorage;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private static final String SELECT_REVIEW_BASE = "SELECT r.id, r.id_user, r.id_film, r.content, r.isPositive, " +
            "(COALESCE(rl1.likes_count, 0) - COALESCE(rl2.dislikes_count, 0)) AS rating " +
            "FROM reviews r " +
            "LEFT JOIN (SELECT id_review, COUNT(id) AS likes_count " +
            "FROM reviews_likes " +
            "WHERE isUseful = TRUE " +
            "GROUP BY id_review) rl1 ON rl1.id_review = r.id " +
            "LEFT JOIN (SELECT id_review, COUNT(id) AS dislikes_count " +
            "FROM reviews_likes " +
            "WHERE isUseful = FALSE " +
            "GROUP BY id_review) rl2 ON rl2.id_review = r.id";
    private static final String ORDER_PART = " ORDER BY rating DESC";

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Optional<Review> get(Long id) {
        Review review;
        try {
            review = jdbcTemplate.queryForObject(SELECT_REVIEW_BASE + " WHERE r.id = ?", MapRowClass::mapRowToReview, id);
        } catch (EmptyResultDataAccessException exp) {
            review = null;
        }
        return Optional.ofNullable(review);
    }

    @Override
    public List<Review> getByFilmId(Integer filmId, Integer count) {
        final MapSqlParameterSource parameters = new MapSqlParameterSource();

        final StringBuilder sqlStrBuilder = new StringBuilder(SELECT_REVIEW_BASE);
        if (nonNull(filmId)) {
            sqlStrBuilder.append(" WHERE r.id_film = :filmId");
            parameters.addValue("filmId", filmId);
        }
        sqlStrBuilder.append(ORDER_PART);
        if (nonNull(count)) {
            sqlStrBuilder.append(" LIMIT :limitValue");
            parameters.addValue("limitValue", count);
        }

        return namedParameterJdbcTemplate.query(sqlStrBuilder.toString(), parameters, MapRowClass::mapRowToReview);
    }

    @Override
    public Long add(final Review review) {
        final String insertSql = "INSERT INTO reviews (id_user, id_film, content, isPositive) " +
                "VALUES (?, ?, ?, ?)";

        final KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            final PreparedStatement stmt = connection.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setLong(ReviewInsertColumn.USER_ID.getColumnIndex(), review.getUserId());
            stmt.setLong(ReviewInsertColumn.FILM_ID.getColumnIndex(), review.getFilmId());
            stmt.setString(ReviewInsertColumn.CONTENT.getColumnIndex(), review.getContent());
            stmt.setBoolean(ReviewInsertColumn.IS_POSITIVE.getColumnIndex(), review.getIsPositive());
            return stmt;
        }, keyHolder);

        Long reviewId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        review.setId(reviewId);

        return reviewId;
    }

    @Override
    public void update(final Review review) {
        final String updateSql = "UPDATE reviews SET content = ?, isPositive = ? WHERE id = ?";

        jdbcTemplate.update(updateSql, review.getContent(), review.getIsPositive(), review.getId());
    }

    @Override
    public void deleteById(Long id) {
        final String sqlQuery = "DELETE FROM reviews WHERE id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public boolean contains(Long id) {
        final String sql = "SELECT EXISTS(SELECT 1 FROM reviews WHERE id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, id);
    }

    @AllArgsConstructor
    private enum ReviewInsertColumn {
        USER_ID(1),
        FILM_ID(2),
        CONTENT(3),
        IS_POSITIVE(4);

        @Getter
        private final int columnIndex;
    }
}