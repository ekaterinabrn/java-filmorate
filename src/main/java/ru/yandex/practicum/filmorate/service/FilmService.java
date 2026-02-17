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
	private static final String VALIDATION_ERROR_PREFIX = "Ошибка валидации: ";
	private static final String FILM_NOT_FOUND = "Фильм с id {} не найден";
	private static final String FILM_NOT_FOUND_MESSAGE = "Фильм с id ";
	private static final String USER_NOT_FOUND_MESSAGE = "Пользователь с id ";

	private final FilmStorage filmStorage;
	private final UserStorage userStorage;


	@Autowired
	public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
		this.filmStorage = filmStorage;
		this.userStorage = userStorage;
	}

	//создать новый фильм

	public Film createFilm(Film film) {
		log.debug("Создаем фильм: {}", film.getName());
		validateFilm(film);
		Film createdFilm = filmStorage.addFilm(film);
		log.debug("Фильм создан  id: {}", createdFilm.getId());
		return createdFilm;
	}

	//обновить  фильм

	public Film updateFilm(Film film) {
		log.debug("Обновляем фильм с id: {}", film.getId());
		validateFilm(film);
		if (film.getId() == null || filmStorage.getFilmById(film.getId()) == null) {
			log.warn("Попытка обновить несуществующий фильм с id: {}", film.getId());
			throw new NotFoundException("Фильм с указанным id не найден");
		}
		Film updatedFilm = filmStorage.updateFilm(film);
		log.debug("Фильм с id {} успешно обновлен", updatedFilm.getId());
		return updatedFilm;
	}

	//получить фильм по идентификатору

	public Film getFilmById(Integer id) {
		Film film = filmStorage.getFilmById(id);
		if (film == null) {
			log.warn(FILM_NOT_FOUND, id);
			throw new NotFoundException(FILM_NOT_FOUND_MESSAGE + id + " не найден");
		}
		return film;
	}

	//список всех фильмов

	public List<Film> getAllFilms() {
		log.debug("Получаем список всех фильмов");
		return filmStorage.getAllFilms();
	}

	//добавлить лайк фильму от пользователя

	public void addLike(Integer filmId, Integer userId) {
		log.debug("Добавляем лайк: пользователь {} ставит лайк фильму {}", userId, filmId);
		Film film = getFilmById(filmId);
		if (userStorage.getUserById(userId) == null) {
			log.warn("Попытка поставить лайк от несуществующего пользователя с id: {}", userId);
			throw new NotFoundException(USER_NOT_FOUND_MESSAGE + userId + " не найден");
		}
		film.getLikes().add(userId.longValue());
	}

	//удалить лайк фильма

	public void removeLike(Integer filmId, Integer userId) {
		log.debug("Удаляем лайк: пользователь {} удаляет лайк фильму {}", userId, filmId);
		Film film = getFilmById(filmId);
		if (userStorage.getUserById(userId) == null) {
			log.warn("Попытка удалить лайк от несуществующего пользователя с id: {}", userId);
			throw new NotFoundException(USER_NOT_FOUND_MESSAGE + userId + " не найден");
		}
		film.getLikes().remove(userId.longValue());
	}

	//список самых популярных фильмов по количеству лайков

	public List<Film> getPopularFilms(Integer count) {
		int limit = (count == null || count <= 0) ? 10 : count;
		log.debug("Получаем список популярных фильмов: {}", limit);
		List<Film> popularFilms = filmStorage.getAllFilms().stream()
				.sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
				.limit(limit)
				.collect(Collectors.toList());
		return popularFilms;
	}

	//валидация данных фильма

	private void validateFilm(Film film) {
		if (film.getName() == null || film.getName().isBlank()) {
			log.error(VALIDATION_ERROR_PREFIX + "название фильма не может быть пустым");
			throw new ValidationException("Название фильма не может быть пустым");
		}
		if (film.getDescription() != null && film.getDescription().length() > 200) {
			log.error(VALIDATION_ERROR_PREFIX + "описание фильма превышает 200 символов");
			throw new ValidationException("Максимальная длина описания — 200 символов");
		}
		LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
		if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(minReleaseDate)) {
			log.error(VALIDATION_ERROR_PREFIX + "дата релиза должна быть не раньше {}", minReleaseDate);
			throw new ValidationException("Дата релиза не раньше 28 декабря 1895 года");
		}
		if (film.getDuration() == null || film.getDuration() <= 0) {
			log.error(VALIDATION_ERROR_PREFIX + "продолжительность фильма должна быть положительным числом");
			throw new ValidationException("Продолжительность фильма должна быть положительным числом");
		}
	}
}
