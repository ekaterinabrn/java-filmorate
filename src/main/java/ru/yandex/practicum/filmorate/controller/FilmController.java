package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/films")
public class FilmController {

	private final FilmService filmService;
	private final FilmMapper filmMapper;

	@Autowired
	public FilmController(FilmService filmService, FilmMapper filmMapper) {
		this.filmService = filmService;
		this.filmMapper = filmMapper;
	}

	@PostMapping
	public ResponseEntity<FilmDto> createFilm(@Valid @RequestBody FilmDto filmDto) {
		Film film = filmMapper.toEntity(filmDto);
		Film created = filmService.createFilm(film);
		return ResponseEntity.status(HttpStatus.CREATED).body(filmMapper.toDto(created));
	}

	@PutMapping
	public ResponseEntity<FilmDto> updateFilm(@Valid @RequestBody FilmDto filmDto) {
		Film film = filmMapper.toEntity(filmDto);
		Film updated = filmService.updateFilm(film);
		return ResponseEntity.ok(filmMapper.toDto(updated));
	}

	@GetMapping
	public ResponseEntity<List<FilmDto>> getAllFilms() {
		return ResponseEntity.ok(
				filmService.getAllFilms().stream()
						.map(filmMapper::toDto)
						.collect(Collectors.toList()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<FilmDto> getFilmById(@PathVariable Integer id) {
		Film film = filmService.getFilmById(id);
		return ResponseEntity.ok(filmMapper.toDto(film));
	}

	@PutMapping("/{id}/like/{userId}")
	public ResponseEntity<Void> addLike(@PathVariable Integer id, @PathVariable Integer userId) {
		filmService.addLike(id, userId);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}/like/{userId}")
	public ResponseEntity<Void> removeLike(@PathVariable Integer id, @PathVariable Integer userId) {
		filmService.removeLike(id, userId);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/popular")
	public ResponseEntity<List<FilmDto>> getPopularFilms(@RequestParam(required = false) Integer count) {
		return ResponseEntity.ok(
				filmService.getPopularFilms(count).stream()
						.map(filmMapper::toDto)
						.collect(Collectors.toList()));
	}
}
