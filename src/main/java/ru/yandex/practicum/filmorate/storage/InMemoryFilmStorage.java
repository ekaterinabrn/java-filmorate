package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;


@Component
@org.springframework.beans.factory.annotation.Qualifier("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
	private final Map<Integer, Film> films = new HashMap<>();
	private int nextId = 1;


	@Override
	public Film addFilm(Film film) {
		film.setId(nextId++);
		films.put(film.getId(), film);
		return film;
	}


	@Override
	public Film updateFilm(Film film) {
		films.put(film.getId(), film);
		return film;
	}


	@Override
	public void deleteFilm(Integer id) {
		films.remove(id);
	}


	@Override
	public Film getFilmById(Integer id) {
		return films.get(id);
	}

	@Override
	public Optional<Film> findFilmById(Integer id) {
		return Optional.ofNullable(films.get(id));
	}

	@Override
	public List<Film> getAllFilms() {
		return new ArrayList<>(films.values());
	}

	@Override
	public void addLike(Integer filmId, Integer userId) {
		Film film = films.get(filmId);
		if (film != null) film.getLikes().add(userId.longValue());
	}

	@Override
	public void removeLike(Integer filmId, Integer userId) {
		Film film = films.get(filmId);
		if (film != null) film.getLikes().remove(userId.longValue());
	}
}
