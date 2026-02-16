package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

//сервис для работы с фильмами

@Slf4j
@Service
public class FilmService {
	private final FilmStorage filmStorage;
	private final UserStorage userStorage;


	@Autowired
	public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
		this.filmStorage = filmStorage;
		this.userStorage = userStorage;
	}

	//создать новый фильм

	public Film createFilm(Film film) {
		validateFilm(film);
		return filmStorage.addFilm(film);
	}

	//обновить  фильм

	public Film updateFilm(Film film) {
		validateFilm(film);
		if (film.getId() == null || filmStorage.getFilmById(film.getId()) == null) {
			throw new NotFoundException("Фильм с указанным id не найден");
		}
		return filmStorage.updateFilm(film);
	}

	//получить фильм по идентификатору

	public Film getFilmById(Integer id) {
		Film film = filmStorage.getFilmById(id);
		if (film == null) {
			throw new NotFoundException("Фильм с id " + id + " не найден");
		}
		return film;
	}

	//список всех фильмов

	public List<Film> getAllFilms() {
		return filmStorage.getAllFilms();
	}

	//добавлить лайк фильму от пользователя

	public void addLike(Integer filmId, Integer userId) {
		Film film = getFilmById(filmId);
		if (userStorage.getUserById(userId) == null) {
			throw new NotFoundException("Пользователь с id " + userId + " не найден");
		}
		film.getLikes().add(userId.longValue());
		log.info("Пользователь с id {} поставил лайк фильму с id {}", userId, filmId);
	}

	//удалить лайк фильма

	public void removeLike(Integer filmId, Integer userId) {
		Film film = getFilmById(filmId);
		if (userStorage.getUserById(userId) == null) {
			throw new NotFoundException("Пользователь с id " + userId + " не найден");
		}
		film.getLikes().remove(userId.longValue());
		log.info("Пользователь с id {} удалил лайк фильму с id {}", userId, filmId);
	}

	//список самых популярных фильмов по количеству лайков

	public List<Film> getPopularFilms(Integer count) {
		int limit = (count == null || count <= 0) ? 10 : count;
		return filmStorage.getAllFilms().stream()
				.sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
				.limit(limit)
				.collect(Collectors.toList());
	}

	//валидация данных фильма

	private void validateFilm(Film film) {
		if (film.getName() == null || film.getName().isBlank()) {
			log.error("Ошибка валидации: название фильма не может быть пустым");
			throw new ValidationException("Название фильма не может быть пустым");
		}
		if (film.getDescription() != null && film.getDescription().length() > 200) {
			log.error("Ошибка валидации: описание фильма превышает 200 символов");
			throw new ValidationException("Максимальная длина описания — 200 символов");
		}
		LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
		if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(minReleaseDate)) {
			log.error("Ошибка валидации: дата релиза должна быть не раньше {}", minReleaseDate);
			throw new ValidationException("Дата релиза не раньше 28 декабря 1895 года");
		}
		if (film.getDuration() == null || film.getDuration() <= 0) {
			log.error("Ошибка валидации: продолжительность фильма должна быть положительным числом");
			throw new ValidationException("Продолжительность фильма должна быть положительным числом");
		}
	}
}
