package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
	/** Идентификатор рейтинга MPA */
	private Integer mpaId;
	/** Рейтинг MPA для ответа API (id, name) */
	private Mpa mpa;
	/** Множество идентификаторов жанров */
	private Set<Integer> genreIds = new HashSet<>();
	/** Жанры для ответа API (массив { id, name }) */
	private List<Genre> genres = new ArrayList<>();
}
