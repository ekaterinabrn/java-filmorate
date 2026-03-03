package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FilmControllerTest {

	private static final String FILM_DESCRIPTION = "Description";
	private static final String FILM_NAME = "nisi eiusmod";
	private static final LocalDate RELEASE_DATE_VALID = LocalDate.of(2000, 1, 1);
	private static final int DURATION_VALID = 120;

	private static final String DESCRIPTION_OVER_200_CHARS = "Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.";
	private static final int DESCRIPTION_MAX_LENGTH = 200;

	private static final LocalDate RELEASE_DATE_BEFORE_MIN = LocalDate.of(1895, 12, 27);
	private static final LocalDate RELEASE_DATE_MIN = LocalDate.of(1895, 12, 28);

	private static final int DURATION_NEGATIVE = -1;
	private static final int DURATION_ZERO = 0;

	private FilmController filmController;
	private Film validFilm;

	@BeforeEach
	void setUp() {
		MpaRepository mpaRepository = mock(MpaRepository.class);
		GenreRepository genreRepository = mock(GenreRepository.class);
		when(mpaRepository.findById(anyInt())).thenReturn(Optional.empty());
		when(genreRepository.findById(anyInt())).thenReturn(Optional.empty());

		FilmService filmService = new FilmService(
				new InMemoryFilmStorage(),
				new InMemoryUserStorage(),
				mpaRepository,
				genreRepository
		);
		filmController = new FilmController(filmService);
		validFilm = new Film();
		validFilm.setName(FILM_NAME);
		validFilm.setDescription(FILM_DESCRIPTION);
		validFilm.setReleaseDate(RELEASE_DATE_VALID);
		validFilm.setDuration(DURATION_VALID);
	}

	@Test
	void createFilmPositiveTest() {
		Film result = filmController.createFilm(validFilm).getBody();
		assertNotNull(result);
		assertNotNull(result.getId());
		assertEquals(FILM_NAME, result.getName());
	}

	@Test
	void createFilm_EmptyNameNegativeTest() {
		validFilm.setName("");
		assertThrows(ValidationException.class, () -> filmController.createFilm(validFilm));
	}

	@Test
	void createFilm_NullNameTestNegativeTest() {
		validFilm.setName(null);
		assertThrows(ValidationException.class, () -> filmController.createFilm(validFilm));
	}

	@Test
	void createFilm_LongDescriptionTest() {
		validFilm.setDescription(DESCRIPTION_OVER_200_CHARS);
		assertThrows(ValidationException.class, () -> filmController.createFilm(validFilm));
	}

	@Test
	void createFilm_Description200CharsTest() {
		validFilm.setDescription("a".repeat(DESCRIPTION_MAX_LENGTH));
		Film result = filmController.createFilm(validFilm).getBody();
		assertNotNull(result);
	}

	@Test
	void createFilm_ReleaseDateBeforeMinDateTest() {
		validFilm.setReleaseDate(RELEASE_DATE_BEFORE_MIN);
		assertThrows(ValidationException.class, () -> filmController.createFilm(validFilm));
	}

	@Test
	void createFilm_ReleaseDateMinDatePositiveTest() {
		validFilm.setReleaseDate(RELEASE_DATE_MIN);
		Film result = filmController.createFilm(validFilm).getBody();
		assertNotNull(result);
	}

	@Test
	void createFilm_WithNullReleaseDateNegativeTest() {
		validFilm.setReleaseDate(null);
		assertThrows(ValidationException.class, () -> filmController.createFilm(validFilm));
	}

	@Test
	void createFilm_NegativeDurationNegativeTest() {
		validFilm.setDuration(DURATION_NEGATIVE);
		assertThrows(ValidationException.class, () -> filmController.createFilm(validFilm));
	}

	@Test
	void createFilm_ZeroDurationNegativeTest() {
		validFilm.setDuration(DURATION_ZERO);
		assertThrows(ValidationException.class, () -> filmController.createFilm(validFilm));
	}

	@Test
	void createFilm_NullDurationNegativeTest() {
		validFilm.setDuration(null);
		assertThrows(ValidationException.class, () -> filmController.createFilm(validFilm));
	}

	@Test
	void createFilm_EmptyRequestNegativeTest() {
		Film emptyFilm = new Film();
		assertThrows(ValidationException.class, () -> filmController.createFilm(emptyFilm));
	}
}
