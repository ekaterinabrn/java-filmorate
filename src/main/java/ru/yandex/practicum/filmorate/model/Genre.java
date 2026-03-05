package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Жанр для API (id, name).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Genre {
	private Integer id;
	private String name;
}
