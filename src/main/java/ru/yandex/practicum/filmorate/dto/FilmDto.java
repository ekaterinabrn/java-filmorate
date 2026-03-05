package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class FilmDto {
	private Integer id;
	private String name;
	private String description;
	private LocalDate releaseDate;
	private Integer duration;
	private Set<Long> likes = new HashSet<>();
	private Integer mpaId;
	private MpaDto mpa;
	private Set<Integer> genreIds = new HashSet<>();
	private List<GenreDto> genres = new ArrayList<>();
}
