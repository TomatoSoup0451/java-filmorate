package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre findGenreById(int id) {
        try {
            return jdbcTemplate.queryForObject("select * from genres where id = ?", this::mapRowToGenre, id);
        } catch (EmptyResultDataAccessException e) {
            throw new IdNotFoundException("Genre with id = " + id + " not found");
        }
    }

    @Override
    public List<Genre> findAllGenres() {
        return jdbcTemplate.query("select * from genres", this::mapRowToGenre);
    }

    @Override
    public List<Genre> findAllGenresByMovieId(long filmId) {
        return jdbcTemplate.query("select * from genres where id in " +
                "(select genre_id from film_genres where film_id = ?)", this::mapRowToGenre, filmId);
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(resultSet.getInt("id"), resultSet.getString("name"));
    }
}
