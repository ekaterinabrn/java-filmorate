package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
public class Film {
	private Integer id;
	private String name;
	private String description;
	private LocalDate releaseDate;
	private Integer duration;
	/** Множество id пользователей, поставивших лайк фильму */
	private Set<Long> likes = new HashSet<>();
}
