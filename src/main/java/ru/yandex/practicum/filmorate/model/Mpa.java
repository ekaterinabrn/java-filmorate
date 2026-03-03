package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Рейтинг MPA для API (id, name). Американская система: G, PG, PG-13, R, NC-17.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mpa {
	private Integer id;
	private String name;
}
