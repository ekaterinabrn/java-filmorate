package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

	private FilmController filmController;
	private Film validFilm;
	private static final String FILM_DESCRIPTION = "Description";
	private static final String FILM_NAME = "nisi eiusmod";

	@BeforeEach
	void setUp() {
		//создаем хранилище и сервис до создания контроллерв
		FilmService filmService = new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage());
		filmController = new FilmController(filmService);
		validFilm = new Film();
		validFilm.setName(FILM_NAME);
		validFilm.setDescription(FILM_DESCRIPTION);
		validFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
		validFilm.setDuration(120);
	}

	@Test
	void createFilmPositiveTest() {
		Film result = filmController.createFilm(validFilm);
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
		validFilm.setDescription("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.");
		assertThrows(ValidationException.class, () -> filmController.createFilm(validFilm));
	}

	@Test
	void createFilm_Description200CharsTest() {
		validFilm.setDescription("a".repeat(200));
		Film result = filmController.createFilm(validFilm);
		assertNotNull(result);
	}

	@Test
	void createFilm_ReleaseDateBeforeMinDateTest() {
		validFilm.setReleaseDate(LocalDate.of(1895, 12, 27));
		assertThrows(ValidationException.class, () -> filmController.createFilm(validFilm));
	}

	@Test
	void createFilm_ReleaseDateMinDatePositiveTest() {
		validFilm.setReleaseDate(LocalDate.of(1895, 12, 28));
		Film result = filmController.createFilm(validFilm);
		assertNotNull(result);
	}

	@Test
	void createFilm_WithNullReleaseDateNegativeTest() {
		validFilm.setReleaseDate(null);
		assertThrows(ValidationException.class, () -> filmController.createFilm(validFilm));
	}

	@Test
	void createFilm_NegativeDurationNegativeTest() {
		validFilm.setDuration(-1);
		assertThrows(ValidationException.class, () -> filmController.createFilm(validFilm));
	}

	@Test
	void createFilm_ZeroDurationNegativeTest() {
		validFilm.setDuration(0);
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
