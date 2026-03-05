package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Mpa;

@Component
public class MpaMapper {

	public MpaDto toDto(Mpa entity) {
		if (entity == null) return null;
		return new MpaDto(entity.getId(), entity.getName());
	}

	public Mpa toEntity(MpaDto dto) {
		if (dto == null) return null;
		return new Mpa(dto.getId(), dto.getName());
	}
}
