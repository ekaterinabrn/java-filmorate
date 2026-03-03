package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("filmDbStorage")
public class FilmRepository extends BaseRepository<Film> implements FilmStorage {

	private static final String FIND_ALL_QUERY = "SELECT film_id, name, description, release_date, duration, mpa_id FROM films ORDER BY film_id";
	private static final String FIND_BY_ID_QUERY = "SELECT film_id, name, description, release_date, duration, mpa_id FROM films WHERE film_id = ?";
	private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
	private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";
	private static final String DELETE_QUERY = "DELETE FROM films WHERE film_id = ?";
	private static final String DELETE_GENRES_QUERY = "DELETE FROM film_genres WHERE film_id = ?";
	private static final String INSERT_GENRE_QUERY = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
	private static final String GENRE_IDS_QUERY = "SELECT genre_id FROM film_genres WHERE film_id = ?";
	private static final String LIKES_QUERY = "SELECT user_id FROM likes WHERE film_id = ?";
	private static final String INSERT_LIKE_QUERY = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
	private static final String DELETE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";

	public FilmRepository(JdbcTemplate jdbc, FilmRowMapper mapper) {
		super(jdbc, mapper);
	}

	@Override
	public Film addFilm(Film film) {
		long id = insert(INSERT_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(),
				film.getDuration(), film.getMpaId());
		film.setId((int) id);
		saveFilmGenres(film);
		loadLikes(film);
		return film;
	}

	@Override
	public Film updateFilm(Film film) {
		jdbc.update(UPDATE_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(),
				film.getDuration(), film.getMpaId(), film.getId());
		jdbc.update(DELETE_GENRES_QUERY, film.getId());
		saveFilmGenres(film);
		Film updated = getFilmById(film.getId());
		loadLikes(updated);
		return updated;
	}

	@Override
	public void deleteFilm(Integer id) {
		delete(DELETE_QUERY, id);
	}

	@Override
	public Film getFilmById(Integer id) {
		List<Film> list = findMany(FIND_BY_ID_QUERY, id);
		if (list.isEmpty()) return null;
		Film film = list.get(0);
		loadGenreIds(film);
		loadLikes(film);
		return film;
	}

	@Override
	public Optional<Film> findFilmById(Integer id) {
		return Optional.ofNullable(getFilmById(id));
	}

	@Override
	public List<Film> getAllFilms() {
		List<Film> list = findMany(FIND_ALL_QUERY);
		list.forEach(f -> {
			loadGenreIds(f);
			loadLikes(f);
		});
		return list;
	}

	@Override
	public void addLike(Integer filmId, Integer userId) {
		jdbc.update(INSERT_LIKE_QUERY, filmId, userId);
	}

	@Override
	public void removeLike(Integer filmId, Integer userId) {
		jdbc.update(DELETE_LIKE_QUERY, filmId, userId);
	}

	private void saveFilmGenres(Film film) {
		if (film.getGenreIds() == null || film.getGenreIds().isEmpty()) return;
		for (Integer genreId : film.getGenreIds()) {
			jdbc.update(INSERT_GENRE_QUERY, film.getId(), genreId);
		}
	}

	private void loadGenreIds(Film film) {
		List<Integer> ids = jdbc.query(GENRE_IDS_QUERY, (rs, rowNum) -> rs.getInt("GENRE_ID"), film.getId());
		film.setGenreIds(new HashSet<>(ids));
	}

	private void loadLikes(Film film) {
		List<Long> ids = jdbc.query(LIKES_QUERY, (rs, rowNum) -> rs.getLong("USER_ID"), film.getId());
		film.setLikes(new HashSet<>(ids));
	}
}
