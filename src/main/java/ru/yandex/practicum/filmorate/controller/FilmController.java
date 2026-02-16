package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
	private final FilmService filmService;


	@Autowired
	public FilmController(FilmService filmService) {
		this.filmService = filmService;
	}

	//создать новый фильм
	@PostMapping
	public Film createFilm(@Valid @RequestBody Film film) {
		log.info("Получен запрос на создание фильма: {}", film.getName());
		Film createdFilm = filmService.createFilm(film);
		log.info("Фильм создан id: {}", createdFilm.getId());
		return createdFilm;
	}

	//обновить  фильм
	@PutMapping
	public Film updateFilm(@Valid @RequestBody Film film) {
		log.info("Получен запрос на обновление фильма с id: {}", film.getId());
		Film updatedFilm = filmService.updateFilm(film);
		log.info("Фильм с id {} обновлен", updatedFilm.getId());
		return updatedFilm;
	}

	//список всех фильмов
	@GetMapping
	public List<Film> getAllFilms() {
		log.info("Получен запрос на получение всех фильмов");
		return filmService.getAllFilms();
	}

	//фильм по его идентификатору

	@GetMapping("/{id}")
	public Film getFilmById(@PathVariable Integer id) {
		log.info("Получен запрос на получение фильма с id: {}", id);
		return filmService.getFilmById(id);
	}

	//добавтть лайк фильму от пользователя
	@PutMapping("/{id}/like/{userId}")
	public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
		log.info("Получен запрос на добавление лайка: пользователь {} ставит лайк фильму {}", userId, id);
		filmService.addLike(id, userId);
	}

	//удалить лайк фильма от пользователя

	@DeleteMapping("/{id}/like/{userId}")
	public void removeLike(@PathVariable Integer id, @PathVariable Integer userId) {
		log.info("Получен запрос на удаление лайка: пользователь {} удаляет лайк фильму {}", userId, id);
		filmService.removeLike(id, userId);
	}

	//список самых популярных фильмов по количеству лайков
	@GetMapping("/popular")
	public List<Film> getPopularFilms(@RequestParam(required = false) Integer count) {
		log.info("Получен запрос на получение популярных фильмов, количество: {}", count != null ? count : 10);
		return filmService.getPopularFilms(count);
	}
}
