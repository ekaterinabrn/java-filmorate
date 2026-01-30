package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

	private UserController userController;
	private User validUser;

	@BeforeEach
	void setUp() {
		userController = new UserController();
		validUser = new User();
		validUser.setEmail("test@example.com");
		validUser.setLogin("testlogin");
		validUser.setName("Test User");
		validUser.setBirthday(LocalDate.of(2000, 1, 1));
	}

	@Test
	void createUser_WithValidData_ReturnsCreatedUser() {
		User result = userController.createUser(validUser);
		assertNotNull(result);
		assertNotNull(result.getId());
		assertEquals("test@example.com", result.getEmail());
	}

	@Test
	void createUser_WithEmptyEmail_ThrowsException() {
		validUser.setEmail("");
		assertThrows(ValidationException.class, () -> userController.createUser(validUser));
	}

	@Test
	void createUser_WithNullEmail_ThrowsException() {
		validUser.setEmail(null);
		assertThrows(ValidationException.class, () -> userController.createUser(validUser));
	}

	@Test
	void createUser_WithEmailWithoutAtSymbol_ThrowsException() {
		validUser.setEmail("invalidemail.com");
		assertThrows(ValidationException.class, () -> userController.createUser(validUser));
	}

	@Test
	void createUser_WithEmptyLogin_ThrowsException() {
		validUser.setLogin("");
		assertThrows(ValidationException.class, () -> userController.createUser(validUser));
	}

	@Test
	void createUser_WithNullLogin_ThrowsException() {
		validUser.setLogin(null);
		assertThrows(ValidationException.class, () -> userController.createUser(validUser));
	}

	@Test
	void createUser_WithLoginContainingSpaces_ThrowsException() {
		validUser.setLogin("test login");
		assertThrows(ValidationException.class, () -> userController.createUser(validUser));
	}

	@Test
	void createUser_WithEmptyName_UsesLoginAsName() {
		validUser.setName("");
		User result = userController.createUser(validUser);
		assertEquals("testlogin", result.getName());
	}

	@Test
	void createUser_WithNullName_UsesLoginAsName() {
		validUser.setName(null);
		User result = userController.createUser(validUser);
		assertEquals("testlogin", result.getName());
	}

	@Test
	void createUser_WithFutureBirthday_ThrowsException() {
		validUser.setBirthday(LocalDate.now().plusDays(1));
		assertThrows(ValidationException.class, () -> userController.createUser(validUser));
	}

	@Test
	void createUser_WithTodayBirthday_ReturnsOk() {
		validUser.setBirthday(LocalDate.now());
		User result = userController.createUser(validUser);
		assertNotNull(result);
	}

	@Test
	void createUser_WithEmptyRequest_ThrowsException() {
		User emptyUser = new User();
		assertThrows(ValidationException.class, () -> userController.createUser(emptyUser));
	}
}
