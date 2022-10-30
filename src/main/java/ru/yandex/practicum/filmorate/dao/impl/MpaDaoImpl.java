package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MpaDaoImpl implements MpaDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Mpa findMpaById(int id) {
        try {
            return jdbcTemplate.queryForObject("select * from mpa where id = ?", this::mapMpaToGenre, id);
        } catch (EmptyResultDataAccessException e) {
            throw new IdNotFoundException("MPA rating with id = " + id + " not found");
        }
    }

    @Override
    public List<Mpa> findAllMpaRatings() {
        return jdbcTemplate.query("select * from mpa", this::mapMpaToGenre);
    }

    private Mpa mapMpaToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Mpa(resultSet.getInt("id"), resultSet.getString("name"));
    }
}
