package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FilmMapper {

	private final MpaMapper mpaMapper;
	private final GenreMapper genreMapper;

	public FilmMapper(MpaMapper mpaMapper, GenreMapper genreMapper) {
		this.mpaMapper = mpaMapper;
		this.genreMapper = genreMapper;
	}

	public FilmDto toDto(Film entity) {
		if (entity == null) return null;
		FilmDto dto = new FilmDto();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setDescription(entity.getDescription());
		dto.setReleaseDate(entity.getReleaseDate());
		dto.setDuration(entity.getDuration());
		dto.setLikes(entity.getLikes() != null ? new HashSet<>(entity.getLikes()) : new HashSet<>());
		dto.setMpaId(entity.getMpaId());
		dto.setMpa(mpaMapper.toDto(entity.getMpa()));
		dto.setGenreIds(entity.getGenreIds() != null ? new HashSet<>(entity.getGenreIds()) : new HashSet<>());
		if (entity.getGenres() != null) {
			List<GenreDto> genreDtos = entity.getGenres().stream()
					.map(genreMapper::toDto)
					.collect(Collectors.toList());
			dto.setGenres(genreDtos);
		} else {
			dto.setGenres(new ArrayList<>());
		}
		return dto;
	}

	public Film toEntity(FilmDto dto) {
		if (dto == null) return null;
		Film film = new Film();
		film.setId(dto.getId());
		film.setName(dto.getName());
		film.setDescription(dto.getDescription());
		film.setReleaseDate(dto.getReleaseDate());
		film.setDuration(dto.getDuration());
		film.setLikes(dto.getLikes() != null ? new HashSet<>(dto.getLikes()) : new HashSet<>());
		Integer mpaId = dto.getMpaId();
		if (mpaId == null && dto.getMpa() != null) mpaId = dto.getMpa().getId();
		film.setMpaId(mpaId);
		film.setMpa(mpaMapper.toEntity(dto.getMpa()));
		if (dto.getGenreIds() != null && !dto.getGenreIds().isEmpty()) {
			film.setGenreIds(new HashSet<>(dto.getGenreIds()));
		} else if (dto.getGenres() != null && !dto.getGenres().isEmpty()) {
			Set<Integer> ids = dto.getGenres().stream()
					.map(GenreDto::getId)
					.filter(id -> id != null)
					.collect(Collectors.toSet());
			film.setGenreIds(ids);
		}
		return film;
	}
}
