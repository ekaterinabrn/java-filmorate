package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/genres")
public class GenreController {

	private final GenreRepository genreRepository;
	private final GenreMapper genreMapper;

	@Autowired
	public GenreController(GenreRepository genreRepository, GenreMapper genreMapper) {
		this.genreRepository = genreRepository;
		this.genreMapper = genreMapper;
	}

	@GetMapping
	public ResponseEntity<List<GenreDto>> getAllGenres() {
		return ResponseEntity.ok(
				genreRepository.findAll().stream()
						.map(genreMapper::toDto)
						.collect(Collectors.toList()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<GenreDto> getGenreById(@PathVariable Integer id) {
		Optional<Genre> genreOpt = genreRepository.findById(id);
		if (genreOpt.isEmpty()) {
			throw new NotFoundException("Жанр с id " + id + " не найден");
		}
		return ResponseEntity.ok(genreMapper.toDto(genreOpt.get()));
	}
}
