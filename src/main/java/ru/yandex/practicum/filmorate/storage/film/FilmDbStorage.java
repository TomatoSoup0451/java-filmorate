package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component("dbFilmStorage")
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final MpaDao mpaDao;
    private final GenreDao genreDao;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film createFilm(Film film) {
        String sqlQueryAddMovie = "insert into films(name, description, duration, release_date, mpa_id) " +
                "values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQueryAddMovie, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setInt(3, film.getDuration());
            stmt.setDate(4, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        long generatedId = keyHolder.getKey().longValue();
        film.setId(generatedId);
        addGenresToMovie(film.getGenres(), film.getId());
        addLikesToMovie(new ArrayList<>(film.getLikes()), film.getId());
        log.info("Movie with id = " + film.getId() + " added");
        return getFilm(film.getId());
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQueryUpdateFilm = "update films set name = ?, description = ?, mpa_id = ?, release_date = ?, duration = ? " +
                "where id = ?";
        int result = jdbcTemplate.update(sqlQueryUpdateFilm, film.getName(), film.getDescription(), film.getMpa().getId(),
                film.getReleaseDate(), film.getDuration(), film.getId());
        if (result == 0)
            throw new IdNotFoundException("Film with id = " + film.getId() + " not found");
        jdbcTemplate.update("delete from film_genres where film_id = ?", film.getId());
        jdbcTemplate.update("delete from likes where film_id = ?", film.getId());
        addGenresToMovie(film.getGenres(), film.getId());
        ArrayList<Long> likes = new ArrayList<>(film.getLikes());
        addLikesToMovie(likes, film.getId());
        return getFilm(film.getId());
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "select id, name, description, mpa_id, release_date, duration " +
                "from films";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film getFilm(long id) {
        String sqlQuery = "select id, name, description, mpa_id, release_date, duration " +
                "from films where id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
        } catch (EmptyResultDataAccessException e) {
            throw new IdNotFoundException("Film with id = " + id + " not found");
        }
    }

    private void addGenresToMovie(List<Genre> genres, long filmId) {
        if (genres == null) {
            return;
        }
        List<Genre> trimmedList = new ArrayList<>(genres.stream()
                .collect(Collectors.toMap(Genre::getId, p -> p, (p, q) -> p)).values());
        String sqlQuery = "insert into film_genres(film_id, genre_id) values(?, ?)";
        jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setLong(2, trimmedList.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return trimmedList.size();
            }
        });


    }

    private void addLikesToMovie(List<Long> userLikes, long filmId) {
        if (userLikes == null) {
            return;
        }

        String sqlQuery = "insert into likes(film_id, user_id) values(?, ?)";
        jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setLong(2, userLikes.get(i));
            }

            @Override
            public int getBatchSize() {
                return userLikes.size();
            }
        });
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .mpa(mpaDao.findMpaById(resultSet.getInt("mpa_id")))
                .genres(genreDao.findAllGenresByMovieId(resultSet.getLong("id")))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .build();
        for (Long id : jdbcTemplate.queryForList("select user_id from likes where film_id = ?",
                Long.class, film.getId())) {
            film.addLike(id);
        }
        return film;
    }

}
