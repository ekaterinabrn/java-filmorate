package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
	private static final int DEFAULT_POPULAR_FILMS_LIMIT = 10;

	private final FilmService filmService;


	@Autowired
	public FilmController(FilmService filmService) {
		this.filmService = filmService;
	}

	//создать новый фильм
	@PostMapping
	public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) {
		log.info("Получен запрос на создание фильма: {}", film.getName());
		Film createdFilm = filmService.createFilm(film);
		log.info("Фильм создан id: {}", createdFilm.getId());
		return ResponseEntity.status(HttpStatus.CREATED).body(createdFilm);
	}

	//обновить  фильм
	@PutMapping
	public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
		log.info("Получен запрос на обновление фильма с id: {}", film.getId());
		Film updatedFilm = filmService.updateFilm(film);
		log.info("Фильм с id {} обновлен", updatedFilm.getId());
		return ResponseEntity.ok(updatedFilm);
	}

	//список всех фильмов
	@GetMapping
	public ResponseEntity<List<Film>> getAllFilms() {
		log.info("Получен запрос на получение всех фильмов");
		return ResponseEntity.ok(filmService.getAllFilms());
	}

	//фильм по его идентификатору

	@GetMapping("/{id}")
	public ResponseEntity<Film> getFilmById(@PathVariable Integer id) {
		log.info("Получен запрос на получение фильма с id: {}", id);
		return ResponseEntity.ok(filmService.getFilmById(id));
	}

	//добавтть лайк фильму от пользователя
	@PutMapping("/{id}/like/{userId}")
	public ResponseEntity<Void> addLike(@PathVariable Integer id, @PathVariable Integer userId) {
		log.info("Получен запрос на добавление лайка: пользователь {} ставит лайк фильму {}", userId, id);
		filmService.addLike(id, userId);
		return ResponseEntity.ok().build();
	}

	//удалить лайк фильма от пользователя

	@DeleteMapping("/{id}/like/{userId}")
	public ResponseEntity<Void> removeLike(@PathVariable Integer id, @PathVariable Integer userId) {
		log.info("Получен запрос на удаление лайка: пользователь {} удаляет лайк фильму {}", userId, id);
		filmService.removeLike(id, userId);
		return ResponseEntity.ok().build();
	}

	//список самых популярных фильмов по количеству лайков
	@GetMapping("/popular")
	public ResponseEntity<List<Film>> getPopularFilms(@RequestParam(required = false) Integer count) {
		log.info("Получен запрос на получение популярных фильмов, количество: {}", count != null ? count : DEFAULT_POPULAR_FILMS_LIMIT);
		return ResponseEntity.ok(filmService.getPopularFilms(count));
	}
}
