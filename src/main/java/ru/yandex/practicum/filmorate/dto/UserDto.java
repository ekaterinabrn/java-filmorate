package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class UserDto {
	private Integer id;
	private String email;
	private String login;
	private String name;
	private LocalDate birthday;
	private Set<Long> friends = new HashSet<>();
}
