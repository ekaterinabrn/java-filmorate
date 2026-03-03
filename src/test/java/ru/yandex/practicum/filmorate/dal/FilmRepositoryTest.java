package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmRepository.class, FilmRowMapper.class})
class FilmRepositoryTest {

	private static final int NONEXISTENT_FILM_ID = 1;

	private static final String FILM_NAME = "Film";
	private static final String FILM_DESC = "Desc";
	private static final LocalDate RELEASE_2000 = LocalDate.of(2000, 1, 1);
	private static final int DURATION_100 = 100;
	private static final int DURATION_90 = 90;
	private static final int MPA_ID_1 = 1;
	private static final int GENRE_ID_1 = 1;
	private static final int GENRE_ID_2 = 2;

	private static final String FILM_NAME_OLD = "Old";
	private static final String FILM_DESC_SHORT = "D";
	private static final String FILM_NAME_NEW = "NewName";

	private static final String FILM_NAME_1 = "Formula1";
	private static final String FILM_NAME_2 = "Lord of the Rings";
	private static final LocalDate RELEASE_2001 = LocalDate.of(2001, 1, 1);

	private static final String FILM_NAME_SINGLE = "F1";
	private static final String FILM_NAME_DELETE = "Del";

	private static final int EXPECTED_TWO_FILMS = 2;

	private final FilmStorage filmStorage;

	@Test
	void findFilmById_emptyDb_returnsEmpty() {
		Optional<Film> opt = filmStorage.findFilmById(NONEXISTENT_FILM_ID);
		assertThat(opt).isEmpty();
	}

	@Test
	void addFilm_and_findFilmById_returnsFilm() {
		Film film = new Film();
		film.setName(FILM_NAME);
		film.setDescription(FILM_DESC);
		film.setReleaseDate(RELEASE_2000);
		film.setDuration(DURATION_100);
		film.setMpaId(MPA_ID_1);
		film.setGenreIds(Set.of(GENRE_ID_1, GENRE_ID_2));
		Film created = filmStorage.addFilm(film);
		assertThat(created.getId()).isNotNull();
		Optional<Film> found = filmStorage.findFilmById(created.getId());
		assertThat(found).isPresent().hasValueSatisfying(f -> {
			assertThat(f.getId()).isEqualTo(created.getId());
			assertThat(f.getName()).isEqualTo(FILM_NAME);
			assertThat(f.getMpaId()).isEqualTo(MPA_ID_1);
			assertThat(f.getGenreIds()).containsExactlyInAnyOrder(GENRE_ID_1, GENRE_ID_2);
		});
	}

	@Test
	void updateFilm_updatesInDb() {
		Film film = new Film();
		film.setName(FILM_NAME_OLD);
		film.setDescription(FILM_DESC_SHORT);
		film.setReleaseDate(RELEASE_2000);
		film.setDuration(DURATION_90);
		film.setMpaId(MPA_ID_1);
		filmStorage.addFilm(film);
		film.setName(FILM_NAME_NEW);
		Film updated = filmStorage.updateFilm(film);
		assertThat(updated.getName()).isEqualTo(FILM_NAME_NEW);
		assertThat(filmStorage.getFilmById(film.getId()).getName()).isEqualTo(FILM_NAME_NEW);
	}

	@Test
	void getAllFilms_returnsAll() {
		Film f1 = new Film();
		f1.setName(FILM_NAME_1);
		f1.setReleaseDate(RELEASE_2000);
		f1.setDuration(DURATION_90);
		filmStorage.addFilm(f1);
		Film f2 = new Film();
		f2.setName(FILM_NAME_2);
		f2.setReleaseDate(RELEASE_2001);
		f2.setDuration(DURATION_100);
		filmStorage.addFilm(f2);
		List<Film> all = filmStorage.getAllFilms();
		assertThat(all).hasSize(EXPECTED_TWO_FILMS);
	}

	@Test
	void getFilmById_returnsFilmWithEmptyLikes() {
		Film film = new Film();
		film.setName(FILM_NAME_SINGLE);
		film.setReleaseDate(RELEASE_2000);
		film.setDuration(DURATION_90);
		film.setMpaId(MPA_ID_1);
		filmStorage.addFilm(film);
		Film loaded = filmStorage.getFilmById(film.getId());
		assertThat(loaded.getLikes()).isEmpty();
	}

	@Test
	void deleteFilm_removesFilm() {
		Film film = new Film();
		film.setName(FILM_NAME_DELETE);
		film.setReleaseDate(RELEASE_2000);
		film.setDuration(DURATION_90);
		filmStorage.addFilm(film);
		filmStorage.deleteFilm(film.getId());
		assertThat(filmStorage.findFilmById(film.getId())).isEmpty();
	}
}
