CREATE TABLE users
(
    id       IDENTITY PRIMARY KEY,
    email    varchar,
    login    varchar,
    name     varchar,
    birthday date
);

CREATE TABLE friends
(
    id                IDENTITY PRIMARY KEY,
    user_id           int,
    friend_id         int,
    friendship_status boolean
);

CREATE TABLE films
(
    id           IDENTITY PRIMARY KEY,
    name         varchar,
    description  varchar(200),
    release_date date,
    duration     int,
    mpa_id       int
);

CREATE TABLE likes
(
    id      IDENTITY PRIMARY KEY,
    film_id int,
    user_id int
);

CREATE TABLE film_genres
(
    id       IDENTITY PRIMARY KEY,
    film_id  int,
    genre_id int
);

CREATE TABLE genres
(
    id   IDENTITY PRIMARY KEY,
    name varchar
);

CREATE TABLE mpa
(
    id   IDENTITY PRIMARY KEY,
    name varchar(5)
);

ALTER TABLE friends
    ADD FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE friends
    ADD FOREIGN KEY (friend_id) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE films
    ADD FOREIGN KEY (mpa_id) REFERENCES mpa (id) ON DELETE CASCADE;

ALTER TABLE likes
    ADD FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE;

ALTER TABLE likes
    ADD FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE film_genres
    ADD FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE;

ALTER TABLE film_genres
    ADD FOREIGN KEY (genre_id) REFERENCES genres (id) ON DELETE CASCADE;
