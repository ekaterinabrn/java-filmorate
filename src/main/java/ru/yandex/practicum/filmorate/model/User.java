package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
public class User {
	private Integer id;
	private String email;
	private String login;
	private String name;
	private LocalDate birthday;
	/** Множество id друзей пользователя для обеспечения уникальности (нельзя добавить одного человека в друзья дважды) */
	private Set<Long> friends = new HashSet<>();
}
