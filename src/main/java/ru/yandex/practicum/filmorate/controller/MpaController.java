package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MpaController {
    private final MpaService mpaService;

    @GetMapping("/mpa/{id}")
    public Mpa findMpaById(@PathVariable int id) {
        return mpaService.findMpaById(id);
    }

    @GetMapping("/mpa")
    public List<Mpa> findAllMpaRatings() {
        return mpaService.findAllMpaRatings();
    }
}
