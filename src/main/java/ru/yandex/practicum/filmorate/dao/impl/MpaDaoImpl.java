package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MpaDaoImpl implements MpaDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Mpa findMpaById(int id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select * from mpa where id = ?", id);
        if (mpaRows.next()) {
            Mpa mpa = new Mpa(
                    mpaRows.getInt("id"),
                    mpaRows.getString("name"));
            return mpa;
        } else {
            throw new IdNotFoundException("MPA rating with id = " + id + " not found");
        }
    }

    @Override
    public List<Mpa> findAllMpaRatings() {
        return jdbcTemplate.query("select * from mpa", (rs, rowNum) ->
                new Mpa(rs.getInt("id"), rs.getString("name")));
    }
}
