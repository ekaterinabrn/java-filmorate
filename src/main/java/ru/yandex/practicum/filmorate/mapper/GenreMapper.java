package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

@Component
public class GenreMapper {

	public GenreDto toDto(Genre entity) {
		if (entity == null) return null;
		return new GenreDto(entity.getId(), entity.getName());
	}

	public Genre toEntity(GenreDto dto) {
		if (dto == null) return null;
		return new Genre(dto.getId(), dto.getName());
	}
}
