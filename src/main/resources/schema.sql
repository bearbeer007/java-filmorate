CREATE TABLE IF NOT EXISTS ratings (
    id SERIAL PRIMARY KEY,
    name VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS genres (
   id SERIAL PRIMARY KEY,
   name varchar(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500) NOT NULL,
    release_date TIMESTAMP NOT NULL,
    duration INT NOT NULL,
    rating_mpa_id INT,
    CONSTRAINT films_ratings_fk FOREIGN KEY (rating_mpa_id) REFERENCES ratings(id)
);

CREATE TABLE IF NOT EXISTS film_genres (
    genre_id INT NOT NULL,
    film_id INT NOT NULL,
    CONSTRAINT film_genres_pk PRIMARY KEY (genre_id, film_id),
    CONSTRAINT film_genres_films_fk FOREIGN KEY (film_id) REFERENCES films(id),
    CONSTRAINT film_genres_genres_fk FOREIGN KEY (genre_id) REFERENCES genres(id)
);

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    login VARCHAR(150) NOT NULL,
    name VARCHAR(150),
    birthday TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS users_friendship (
    user_id INT NOT NULL,
    friend_id INT NOT NULL,
    CONSTRAINT users_friendship_pk PRIMARY KEY (user_id, friend_id),
    CONSTRAINT users_friendship_users_fk FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT users_friendship_users_fk_1 FOREIGN KEY (friend_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS like_films (
    film_id INT NOT NULL,
    user_id INT NOT NULL,
    CONSTRAINT like_films_pk PRIMARY KEY (film_id, user_id),
    CONSTRAINT like_films_films_fk FOREIGN KEY (film_id) REFERENCES films(id),
    CONSTRAINT like_films_users_fk FOREIGN KEY (user_id) REFERENCES users(id)
);


