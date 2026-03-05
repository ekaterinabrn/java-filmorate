package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;


public interface FilmStorage {

	Film addFilm(Film film);

	Film updateFilm(Film film);

	void deleteFilm(Integer id);

	Film getFilmById(Integer id);

	Optional<Film> findFilmById(Integer id);

	List<Film> getAllFilms();

	void addLike(Integer filmId, Integer userId);

	void removeLike(Integer filmId, Integer userId);
}
