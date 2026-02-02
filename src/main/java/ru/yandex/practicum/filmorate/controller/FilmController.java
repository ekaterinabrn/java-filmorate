package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
	private final Map<Integer, Film> films = new HashMap<>();
	private int nextId = 1;
	private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

	//добавление нового фильма

	@PostMapping
	public Film createFilm(@Valid @RequestBody Film film) {
		log.info("Получен запрос на создание фильма: {}", film.getName());
		validateFilm(film);
		film.setId(nextId++);
		films.put(film.getId(), film);
		log.info("Фильм создан id: {}", film.getId());
		return film;
	}

	//обновление существующего фильма

	@PutMapping
	public Film updateFilm(@Valid @RequestBody Film film) {
		log.info("Получен запрос на обновление фильма с id: {}", film.getId());
		validateFilm(film);
		if (film.getId() == null || !films.containsKey(film.getId())) {
			log.error("Фильм с id {} не найден", film.getId());
			throw new ValidationException("Фильм с указанным id не найден");
		}
		films.put(film.getId(), film);
		log.info("Фильм с id {} обновлен", film.getId());
		return film;
	}

	//получение списка всех фильмов.
	@GetMapping
	public List<Film> getAllFilms() {
		log.info("Получен запрос на получение всех фильмов. Всего в коллекции : {}", films.size());
		return new ArrayList<>(films.values());
	}


	// проверка  названия, описания, даты релиза и продолжительность
	void validateFilm(Film film) {
		if (film.getName() == null || film.getName().isBlank()) {
			log.error("Ошибка: название фильма не может быть пустым");
			throw new ValidationException("Название фильма не может быть пустым");
		}
		if (film.getDescription() != null && film.getDescription().length() > 200) {
			log.error("Ошибка: описание фильма превышает 200 символов");
			throw new ValidationException("Максимальная длина описания — 200 символов");
		}
		if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
			log.error("Ошибка: дата релиза должна быть не раньше {}", MIN_RELEASE_DATE);
			throw new ValidationException("Дата релиза не раньше 28 декабря 1895 года");
		}
		if (film.getDuration() == null || film.getDuration() <= 0) {
			log.error("Ошибка: продолжительность фильма должна быть положительным числом");
			throw new ValidationException("Продолжительность фильма должна быть положительным числом");
		}
	}
}
