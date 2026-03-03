package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы с фильмами
 */
@Slf4j
@Service
public class FilmService {
	private static final String VALIDATION_ERROR_PREFIX = "Ошибка валидации: ";
	private static final String FILM_NOT_FOUND = "Фильм с id {} не найден";
	private static final String FILM_NOT_FOUND_MESSAGE = "Фильм с id ";
	private static final String USER_NOT_FOUND_MESSAGE = "Пользователь с id ";
	private static final String MPA_NOT_FOUND_MESSAGE = "Рейтинг MPA с id ";
	private static final String GENRE_NOT_FOUND_MESSAGE = "Жанр с id ";

	private static final int DEFAULT_POPULAR_FILMS_LIMIT = 10;
	private static final int MAX_FILM_DESCRIPTION_LENGTH = 200;
	private static final int MIN_RELEASE_YEAR = 1895;
	private static final int MIN_RELEASE_MONTH = 12;
	private static final int MIN_RELEASE_DAY = 28;
	private static final int MIN_DURATION_VALUE = 0;

	private final FilmStorage filmStorage;
	private final UserStorage userStorage;
	private final MpaRepository mpaRepository;
	private final GenreRepository genreRepository;

	@Autowired
	public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
					   @Qualifier("userDbStorage") UserStorage userStorage,
					   MpaRepository mpaRepository,
					   GenreRepository genreRepository) {
		this.filmStorage = filmStorage;
		this.userStorage = userStorage;
		this.mpaRepository = mpaRepository;
		this.genreRepository = genreRepository;
	}

	/** Принимаем запрос и с mpa/genres: подставляем mpaId и genreIds для сохранения в БД */
	private void normalizeFilmRequest(Film film) {
		if (film.getMpaId() == null && film.getMpa() != null && film.getMpa().getId() != null) {
			film.setMpaId(film.getMpa().getId());
		}
		if ((film.getGenreIds() == null || film.getGenreIds().isEmpty()) && film.getGenres() != null && !film.getGenres().isEmpty()) {
			film.setGenreIds(film.getGenres().stream()
					.map(Genre::getId)
					.filter(id -> id != null)
					.collect(Collectors.toSet()));
		}
	}

	/** Заполняет mpa и genres для ответа API */
	private void enrichFilmWithMpaAndGenres(Film film) {
		if (film.getMpaId() != null) {
			film.setMpa(mpaRepository.findById(film.getMpaId()).orElse(null));
		}
		List<Genre> genreList = new ArrayList<>();
		if (film.getGenreIds() != null) {
			for (Integer genreId : film.getGenreIds()) {
				genreRepository.findById(genreId).ifPresent(genreList::add);
			}
		}
		film.setGenres(genreList);
	}

	/**
	 * Создать новый фильм
	 *
	 * @param film фильм для создания
	 * @return созданный фильм с присвоенным id
	 */
	public Film createFilm(Film film) {
		log.debug("Создаем фильм: {}", film.getName());
		normalizeFilmRequest(film);
		validateFilm(film);
		Film createdFilm = filmStorage.addFilm(film);
		enrichFilmWithMpaAndGenres(createdFilm);
		log.debug("Фильм создан  id: {}", createdFilm.getId());
		return createdFilm;
	}

	/**
	 * Обновить фильм
	 *
	 * @param film фильм для обновления
	 * @return обновленный фильм
	 */
	public Film updateFilm(Film film) {
		log.debug("Обновляем фильм с id: {}", film.getId());
		normalizeFilmRequest(film);
		validateFilm(film);
		if (film.getId() == null || filmStorage.getFilmById(film.getId()) == null) {
			log.warn("Попытка обновить несуществующий фильм с id: {}", film.getId());
			throw new NotFoundException("Фильм с указанным id не найден");
		}
		Film updatedFilm = filmStorage.updateFilm(film);
		enrichFilmWithMpaAndGenres(updatedFilm);
		log.debug("Фильм с id {} успешно обновлен", updatedFilm.getId());
		return updatedFilm;
	}

	/**
	 * Получить фильм по идентификатору
	 *
	 * @param id идентификатор фильма
	 * @return найденный фильм
	 */
	public Film getFilmById(Integer id) {
		Film film = filmStorage.getFilmById(id);
		if (film == null) {
			log.warn(FILM_NOT_FOUND, id);
			throw new NotFoundException(FILM_NOT_FOUND_MESSAGE + id + " не найден");
		}
		enrichFilmWithMpaAndGenres(film);
		return film;
	}

	/**
	 * Список всех фильмов
	 *
	 * @return список всех фильмов
	 */
	public List<Film> getAllFilms() {
		log.debug("Получаем список всех фильмов");
		List<Film> films = filmStorage.getAllFilms();
		films.forEach(this::enrichFilmWithMpaAndGenres);
		return films;
	}

	/**
	 * Добавить лайк фильму от пользователя
	 *
	 * @param filmId идентификатор фильма
	 * @param userId идентификатор пользователя
	 */
	public void addLike(Integer filmId, Integer userId) {
		log.debug("Добавляем лайк: пользователь {} ставит лайк фильму {}", userId, filmId);
		getFilmById(filmId);
		if (userStorage.getUserById(userId) == null) {
			throw new NotFoundException(USER_NOT_FOUND_MESSAGE + userId + " не найден");
		}
		filmStorage.addLike(filmId, userId);
	}

	/**
	 * Удалить лайк фильма
	 */
	public void removeLike(Integer filmId, Integer userId) {
		log.debug("Удаляем лайк: пользователь {} удаляет лайк фильму {}", userId, filmId);
		getFilmById(filmId);
		if (userStorage.getUserById(userId) == null) {
			throw new NotFoundException(USER_NOT_FOUND_MESSAGE + userId + " не найден");
		}
		filmStorage.removeLike(filmId, userId);
	}

	/**
	 * Список самых популярных фильмов по количеству лайков
	 *
	 * @param count количество фильмов для возврата (если null или <= 0, возвращается 10)
	 * @return список популярных фильмов
	 */
	public List<Film> getPopularFilms(Integer count) {
		int limit = (count == null || count <= MIN_DURATION_VALUE) ? DEFAULT_POPULAR_FILMS_LIMIT : count;
		log.debug("Получаем список популярных фильмов: {}", limit);
		List<Film> popularFilms = filmStorage.getAllFilms().stream()
				.sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
				.limit(limit)
				.collect(Collectors.toList());
		popularFilms.forEach(this::enrichFilmWithMpaAndGenres);
		return popularFilms;
	}

	/**
	 * Валидация данных фильма
	 *
	 * @param film фильм для валидации
	 */
	private void validateFilm(Film film) {
		if (film.getName() == null || film.getName().isBlank()) {
			log.error(VALIDATION_ERROR_PREFIX + "название фильма не может быть пустым");
			throw new ValidationException("Название фильма не может быть пустым");
		}
		if (film.getDescription() != null && film.getDescription().length() > MAX_FILM_DESCRIPTION_LENGTH) {
			log.error(VALIDATION_ERROR_PREFIX + "описание фильма превышает {} символов", MAX_FILM_DESCRIPTION_LENGTH);
			throw new ValidationException("Максимальная длина описания — " + MAX_FILM_DESCRIPTION_LENGTH + " символов");
		}
		LocalDate minReleaseDate = LocalDate.of(MIN_RELEASE_YEAR, MIN_RELEASE_MONTH, MIN_RELEASE_DAY);
		if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(minReleaseDate)) {
			log.error(VALIDATION_ERROR_PREFIX + "дата релиза должна быть не раньше {}", minReleaseDate);
			throw new ValidationException("Дата релиза не раньше " + MIN_RELEASE_DAY + " декабря " + MIN_RELEASE_YEAR + " года");
		}
		if (film.getDuration() == null || film.getDuration() <= MIN_DURATION_VALUE) {
			log.error(VALIDATION_ERROR_PREFIX + "продолжительность фильма должна быть положительным числом");
			throw new ValidationException("Продолжительность фильма должна быть положительным числом");
		}
		validateMpaAndGenresExist(film);
	}

	/**
	 * Проверяет существование MPA и жанров в БД. При отсутствии — 404.
	 */
	private void validateMpaAndGenresExist(Film film) {
		if (film.getMpaId() != null && mpaRepository.findById(film.getMpaId()).isEmpty()) {
			log.warn("Рейтинг MPA с id {} не найден", film.getMpaId());
			throw new NotFoundException(MPA_NOT_FOUND_MESSAGE + film.getMpaId() + " не найден");
		}
		if (film.getGenreIds() != null) {
			for (Integer genreId : film.getGenreIds()) {
				if (genreRepository.findById(genreId).isEmpty()) {
					log.warn("Жанр с id {} не найден", genreId);
					throw new NotFoundException(GENRE_NOT_FOUND_MESSAGE + genreId + " не найден");
				}
			}
		}
	}
}
