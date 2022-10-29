package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre findGenreById(int id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select * from genres where id = ?", id);
        if (genreRows.next()) {
            return new Genre(
                    genreRows.getInt("id"),
                    genreRows.getString("name"));
        } else {
            throw new IdNotFoundException("Genre with id = " + id + " not found");
        }
    }

    @Override
    public List<Genre> findAllGenres() {
        return jdbcTemplate.query("select * from genres", (rs, rowNum) ->
                new Genre(rs.getInt("id"), rs.getString("name")));
    }

    @Override
    public List<Genre> findAllGenresByMovieId(long filmId) {
        return jdbcTemplate.query("select * from genres where id in " +
                "(select genre_id from film_genres where film_id = ?)", (rs, rowNum) ->
                new Genre(rs.getInt("id"), rs.getString("name")), filmId);
    }
}
