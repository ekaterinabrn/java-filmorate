package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

	private static final String USER_LOGIN = "dolore";
	private static final String USER_NAME = "Nick Name";
	private static final String USER_EMAIL = "mail@mail.ru";
	private static final LocalDate USER_BIRTHDAY = LocalDate.of(1990, 8, 20);

	private static final String EMAIL_INVALID_NO_AT = "invalidemail.ru";
	private static final String LOGIN_WITH_SPACES = "test login";

	private UserController userController;
	private User validUser;

	@BeforeEach
	void setUp() {
		UserService userService = new UserService(new InMemoryUserStorage());
		userController = new UserController(userService);
		validUser = new User();
		validUser.setEmail(USER_EMAIL);
		validUser.setLogin(USER_LOGIN);
		validUser.setName(USER_NAME);
		validUser.setBirthday(USER_BIRTHDAY);
	}

	@Test
	void createUser_WithValidData_ReturnsCreatedUser() {
		User result = userController.createUser(validUser).getBody();
		assertNotNull(result);
		assertNotNull(result.getId());
		assertEquals(USER_EMAIL, result.getEmail());
	}

	@Test
	void createUser_EmptyEmailNegativeTest() {
		validUser.setEmail("");
		assertThrows(ValidationException.class, () -> userController.createUser(validUser));
	}

	@Test
	void createUser_NullEmailNegativeTest() {
		validUser.setEmail(null);
		assertThrows(ValidationException.class, () -> userController.createUser(validUser));
	}

	@Test
	void createUser_EmailWithoutAtSymbolNegativeTest() {
		validUser.setEmail(EMAIL_INVALID_NO_AT);
		assertThrows(ValidationException.class, () -> userController.createUser(validUser));
	}

	@Test
	void createUser_EmptyLoginNegativeTest() {
		validUser.setLogin("");
		assertThrows(ValidationException.class, () -> userController.createUser(validUser));
	}

	@Test
	void createUser_NullLoginNegativeTest() {
		validUser.setLogin(null);
		assertThrows(ValidationException.class, () -> userController.createUser(validUser));
	}

	@Test
	void createUser_LoginContainingSpacesNegativeTest() {
		validUser.setLogin(LOGIN_WITH_SPACES);
		assertThrows(ValidationException.class, () -> userController.createUser(validUser));
	}

	@Test
	void createUser_WithEmptyNamePositiveTest() {
		validUser.setName("");
		User result = userController.createUser(validUser).getBody();
		assertEquals(USER_LOGIN, result.getName());
	}

	@Test
	void createUser_UsesLoginAsNamePositiveTest() {
		validUser.setName(null);
		User result = userController.createUser(validUser).getBody();
		assertEquals(USER_LOGIN, result.getName());
	}

	@Test
	void createUser_FutureBirthdayNegativeTest() {
		validUser.setBirthday(LocalDate.now().plusDays(1));
		assertThrows(ValidationException.class, () -> userController.createUser(validUser));
	}

	@Test
	void createUser_TodayBirthdayPositiveTest() {
		validUser.setBirthday(LocalDate.now());
		User result = userController.createUser(validUser).getBody();
		assertNotNull(result);
	}

	@Test
	void createUserEmptyRequestNegativeTest() {
		User emptyUser = new User();
		assertThrows(ValidationException.class, () -> userController.createUser(emptyUser));
	}
}
