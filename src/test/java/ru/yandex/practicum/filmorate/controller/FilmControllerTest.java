package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

	private FilmController filmController;
	private Film validFilm;

	@BeforeEach
	void setUp() {
		filmController = new FilmController();
		validFilm = new Film();
		validFilm.setName("Test Film");
		validFilm.setDescription("Test Description");
		validFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
		validFilm.setDuration(120);
	}

	@Test
	void createFilm_WithValidData_ReturnsCreatedFilm() {
		Film result = filmController.createFilm(validFilm);
		assertNotNull(result);
		assertNotNull(result.getId());
		assertEquals("Test Film", result.getName());
	}

	@Test
	void createFilm_WithEmptyName_ThrowsException() {
		validFilm.setName("");
		assertThrows(ValidationException.class, () -> filmController.createFilm(validFilm));
	}

	@Test
	void createFilm_WithNullName_ThrowsException() {
		validFilm.setName(null);
		assertThrows(ValidationException.class, () -> filmController.createFilm(validFilm));
	}

	@Test
	void createFilm_WithDescriptionTooLong_ThrowsException() {
		validFilm.setDescription("a".repeat(201));
		assertThrows(ValidationException.class, () -> filmController.createFilm(validFilm));
	}

	@Test
	void createFilm_WithDescriptionExactly200Chars_ReturnsOk() {
		validFilm.setDescription("a".repeat(200));
		Film result = filmController.createFilm(validFilm);
		assertNotNull(result);
	}

	@Test
	void createFilm_WithReleaseDateBeforeMinDate_ThrowsException() {
		validFilm.setReleaseDate(LocalDate.of(1895, 12, 27));
		assertThrows(ValidationException.class, () -> filmController.createFilm(validFilm));
	}

	@Test
	void createFilm_WithReleaseDateMinDate_ReturnsOk() {
		validFilm.setReleaseDate(LocalDate.of(1895, 12, 28));
		Film result = filmController.createFilm(validFilm);
		assertNotNull(result);
	}

	@Test
	void createFilm_WithNullReleaseDate_ThrowsException() {
		validFilm.setReleaseDate(null);
		assertThrows(ValidationException.class, () -> filmController.createFilm(validFilm));
	}

	@Test
	void createFilm_WithNegativeDuration_ThrowsException() {
		validFilm.setDuration(-1);
		assertThrows(ValidationException.class, () -> filmController.createFilm(validFilm));
	}

	@Test
	void createFilm_WithZeroDuration_ThrowsException() {
		validFilm.setDuration(0);
		assertThrows(ValidationException.class, () -> filmController.createFilm(validFilm));
	}

	@Test
	void createFilm_WithNullDuration_ThrowsException() {
		validFilm.setDuration(null);
		assertThrows(ValidationException.class, () -> filmController.createFilm(validFilm));
	}

	@Test
	void createFilm_WithEmptyRequest_ThrowsException() {
		Film emptyFilm = new Film();
		assertThrows(ValidationException.class, () -> filmController.createFilm(emptyFilm));
	}
}
