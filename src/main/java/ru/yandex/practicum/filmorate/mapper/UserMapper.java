package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;

@Component
public class UserMapper {

	public UserDto toDto(User entity) {
		if (entity == null) return null;
		UserDto dto = new UserDto();
		dto.setId(entity.getId());
		dto.setEmail(entity.getEmail());
		dto.setLogin(entity.getLogin());
		dto.setName(entity.getName());
		dto.setBirthday(entity.getBirthday());
		dto.setFriends(entity.getFriends() != null ? new HashSet<>(entity.getFriends()) : new HashSet<>());
		return dto;
	}

	public User toEntity(UserDto dto) {
		if (dto == null) return null;
		User user = new User();
		user.setId(dto.getId());
		user.setEmail(dto.getEmail());
		user.setLogin(dto.getLogin());
		user.setName(dto.getName());
		user.setBirthday(dto.getBirthday());
		user.setFriends(dto.getFriends() != null ? new HashSet<>(dto.getFriends()) : new HashSet<>());
		return user;
	}
}
