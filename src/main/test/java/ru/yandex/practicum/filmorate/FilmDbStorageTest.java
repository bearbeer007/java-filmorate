package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testFindFilmById() {
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        Mpa mpa = Mpa.builder()
                .id(1)
                .name("R").build();
        Film newFilm = Film.builder()
                .name("name")
                .description("description")
                .duration(120)
                .releaseDate(LocalDate.of(2020, 12, 12))
                .mpa(mpa)
                .build();

        filmStorage.addFilm(newFilm);
        if (filmStorage.findFilmById(1L).isPresent()) {
            Film savedFilm = filmStorage.findFilmById(1L).get();
            assertEquals(newFilm.getId(), savedFilm.getId());
            assertEquals(newFilm.getName(), savedFilm.getName());
            assertEquals(newFilm.getDuration(), savedFilm.getDuration());
            assertEquals(newFilm.getReleaseDate(), savedFilm.getReleaseDate());
        }
    }

    @Test
    public void testUpdateFilm() {
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        Mpa mpa = Mpa.builder()
                .id(1)
                .name("R").build();
        Film newFilm = Film.builder()
                .name("name")
                .description("description")
                .duration(120)
                .releaseDate(LocalDate.of(2020, 12, 12))
                .mpa(mpa)
                .build();
        filmStorage.addFilm(newFilm);
        Film newFilm2 = Film.builder()
                .id(1L)
                .name("bla")
                .description("bla")
                .duration(120)
                .releaseDate(LocalDate.of(2020, 12, 12))
                .mpa(mpa)
                .build();
        var updatedFilm = filmStorage.updateFilm(newFilm2);

        assertEquals(updatedFilm.getName(), "bla");
        assertEquals(updatedFilm.getDescription(), "bla");
    }

    @Test
    public void testGetAllFilms() {
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        Mpa mpa = Mpa.builder()
                .id(1)
                .name("R").build();
        Film newFilm1 = Film.builder()
                .name("name")
                .description("description")
                .duration(120)
                .releaseDate(LocalDate.of(2020, 12, 12))
                .mpa(mpa)
                .build();
        Film newFilm2 = Film.builder()
                .name("name2")
                .description("description2")
                .duration(120)
                .releaseDate(LocalDate.of(2010, 12, 12))
                .mpa(mpa)
                .build();
        var createdFilm1 = filmStorage.addFilm(newFilm1);
        var createdFilm2 = filmStorage.addFilm(newFilm2);
        List<Film> films = filmStorage.getAllFilms();
        var filmFromDb1 = filmStorage.findFilmById(createdFilm1.getId()).get();
        var filmFromDb2 = filmStorage.findFilmById(createdFilm2.getId()).get();
        List<Film> filmsDb = new ArrayList<>();
        filmsDb.add(filmFromDb1);
        filmsDb.add(filmFromDb2);

        assertEquals(2, films.size());
        assertEquals(films, filmsDb);
    }

    @Test
    public void testAddLikeFilms() {
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        Mpa mpa = Mpa.builder()
                .id(1)
                .name("R").build();
        Film newFilm1 = Film.builder()
                .name("name")
                .description("description")
                .duration(120)
                .releaseDate(LocalDate.of(2020, 12, 12))
                .mpa(mpa)
                .build();
        var createdFilm1 = filmStorage.addFilm(newFilm1);

        User newUser = User.builder()
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User createdUser = userStorage.addUser(newUser);

        filmStorage.addLike(createdFilm1.getId(), createdUser.getId());
        assertTrue(likeAdded(createdFilm1.getId(), createdUser.getId()));
    }

    @Test
    public void testDeleteLikeFilms() {
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        Mpa mpa = Mpa.builder()
                .id(1)
                .name("R").build();
        Film newFilm1 = Film.builder()
                .name("name")
                .description("description")
                .duration(120)
                .releaseDate(LocalDate.of(2020, 12, 12))
                .mpa(mpa)
                .build();
        var createdFilm1 = filmStorage.addFilm(newFilm1);

        User newUser = User.builder()
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User createdUser = userStorage.addUser(newUser);

        filmStorage.addLike(createdFilm1.getId(), createdUser.getId());
        assertTrue(likeAdded(createdFilm1.getId(), createdUser.getId()));
        filmStorage.deleteLike(createdFilm1.getId(), createdUser.getId());
        assertTrue(likeRemoved(createdFilm1.getId(), createdUser.getId()));
    }

    @Test
    public void testGetPopularFilms() {
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        Mpa mpa = Mpa.builder()
                .id(1)
                .name("R").build();
        Film newFilm1 = Film.builder()
                .name("name")
                .description("description")
                .duration(120)
                .releaseDate(LocalDate.of(2020, 12, 12))
                .mpa(mpa)
                .build();
        Film newFilm2 = Film.builder()
                .name("name2")
                .description("description2")
                .duration(10)
                .releaseDate(LocalDate.of(2010, 12, 12))
                .mpa(mpa)
                .build();
        Film newFilm3 = Film.builder()
                .name("name3")
                .description("description3")
                .duration(150)
                .releaseDate(LocalDate.of(2010, 12, 12))
                .mpa(mpa)
                .build();
        var createdFilm1 = filmStorage.addFilm(newFilm1);
        var createdFilm2 = filmStorage.addFilm(newFilm2);
        var createdFilm3 = filmStorage.addFilm(newFilm3);

        User newUser = User.builder()
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User createdUser = userStorage.addUser(newUser);
        User newUser2 = User.builder()
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User createdUser2 = userStorage.addUser(newUser2);

        filmStorage.addLike(createdFilm3.getId(), createdUser.getId());
        filmStorage.addLike(createdFilm3.getId(), createdUser2.getId());
        filmStorage.addLike(createdFilm2.getId(), createdUser2.getId());

        List<Film> films = filmStorage.getPopularFilms(3L);
        List<Film> filmsDb = new ArrayList<>();

        var filmFromDb1 = filmStorage.findFilmById(createdFilm1.getId()).get();
        var filmFromDb2 = filmStorage.findFilmById(createdFilm2.getId()).get();
        var filmFromDb3 = filmStorage.findFilmById(createdFilm3.getId()).get();
        filmsDb.add(filmFromDb3);
        filmsDb.add(filmFromDb2);
        assertEquals(films, filmsDb);
    }

    @Test
    public void testAddGenreToFilm() {
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        Mpa mpa = Mpa.builder()
                .id(1)
                .name("R").build();
        Film newFilm1 = Film.builder()
                .name("name")
                .description("description")
                .duration(120)
                .releaseDate(LocalDate.of(2020, 12, 12))
                .mpa(mpa)
                .build();
        var createdFilm1 = filmStorage.addFilm(newFilm1);

        filmStorage.addGenreToFilm(createdFilm1.getId(), 1);
        assertTrue(genreAdded(createdFilm1.getId(), 1));
    }

    private boolean likeAdded(Long filmId, Long userId) {
        String sql = "SELECT COUNT(1) FROM like_films where film_id = ? and user_id = ?";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, filmId, userId));
    }

    private boolean likeRemoved(Long filmId, Long userId) {
        String sql = "SELECT COUNT(1) FROM like_films where film_id = ? and user_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, filmId, userId) == 0;
    }

    private boolean genreAdded(Long filmId, Integer genreId) {
        String sql = "SELECT COUNT(1) FROM film_genres where film_id = ? and genre_id = ?";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, filmId, genreId));
    }
}




