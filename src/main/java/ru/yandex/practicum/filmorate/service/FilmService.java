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
		log.trace("Ищем фильм с id: {}", id);
		Film film = filmStorage.getFilmById(id);
		if (film == null) {
			log.warn(FILM_NOT_FOUND, id);
			throw new NotFoundException(FILM_NOT_FOUND_MESSAGE + id + " не найден");
		}
		log.trace("Фильм с id {} найден: {}", id, film.getName());
		return film;
	}

	//список всех фильмов

	public List<Film> getAllFilms() {
		log.debug("Получаем список всех фильмов");
		List<Film> films = filmStorage.getAllFilms();
		log.trace("Найдено фильмов: {}", films.size());
		return films;
	}

	//добавлить лайк фильму от пользователя

	public void addLike(Integer filmId, Integer userId) {
		log.debug("Добавляем лайк: пользователь {} ставит лайк фильму {}", userId, filmId);
		Film film = getFilmById(filmId);
		if (userStorage.getUserById(userId) == null) {
			log.warn("Попытка поставить лайк от несуществующего пользователя с id: {}", userId);
			throw new NotFoundException(USER_NOT_FOUND_MESSAGE + userId + " не найден");
		}
		log.trace("Текущее количество лайков у фильма {}: {}", filmId, film.getLikes().size());
		film.getLikes().add(userId.longValue());
		log.info("Пользователь с id {} поставил лайк фильму с id {}", userId, filmId);
		log.trace("Количество лайков после добавления у фильма {}: {}", filmId, film.getLikes().size());
	}

	//удалить лайк фильма

	public void removeLike(Integer filmId, Integer userId) {
		log.debug("Удаляем лайк: пользователь {} удаляет лайк фильму {}", userId, filmId);
		Film film = getFilmById(filmId);
		if (userStorage.getUserById(userId) == null) {
			log.warn("Попытка удалить лайк от несуществующего пользователя с id: {}", userId);
			throw new NotFoundException(USER_NOT_FOUND_MESSAGE + userId + " не найден");
		}
		log.trace("Текущее количество лайков у фильма {}: {}", filmId, film.getLikes().size());
		film.getLikes().remove(userId.longValue());
		log.info("Пользователь с id {} удалил лайк фильму с id {}", userId, filmId);
		log.trace("Количество лайков после удаления у фильма {}: {}", filmId, film.getLikes().size());
	}

	//список самых популярных фильмов по количеству лайков

	public List<Film> getPopularFilms(Integer count) {
		int limit = (count == null || count <= 0) ? 10 : count;
		log.debug("Получаем список популярных фильмов: {}", limit);
		List<Film> allFilms = filmStorage.getAllFilms();
		log.trace("Всего фильмов в хранилище: {}", allFilms.size());
		List<Film> popularFilms = allFilms.stream()
				.sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
				.limit(limit)
				.collect(Collectors.toList());
		log.debug("Получен список популярных фильмов , количество: {}", popularFilms.size());
		log.trace("Популярные фильмы отсортированы по количеству лайков");
		return popularFilms;
	}

	//валидация данных фильма

	private void validateFilm(Film film) {
		log.trace("Проверка валидации фильма: {}", film.getName());
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
		log.trace("Валидация фильма пройдена успешно");
	}
}
