package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController {

	private final GenreRepository genreRepository;

	@Autowired
	public GenreController(GenreRepository genreRepository) {
		this.genreRepository = genreRepository;
	}

	/**
	 * Список всех жанров
	 */
	@GetMapping
	public ResponseEntity<List<Genre>> getAllGenres() {
		log.info("Получен запрос на получение списка жанров");
		return ResponseEntity.ok(genreRepository.findAll());
	}

	/**
	 * Жанр по идентификатору
	 *
	 * @param id идентификатор жанра
	 * @return найденный жанр
	 */
	@GetMapping("/{id}")
	public ResponseEntity<Genre> getGenreById(@PathVariable Integer id) {
		log.info("Получен запрос на получение жанра с id: {}", id);
		return genreRepository.findById(id)
				.map(ResponseEntity::ok)
				.orElseThrow(() -> new NotFoundException("Жанр с id " + id + " не найден"));
	}
}
