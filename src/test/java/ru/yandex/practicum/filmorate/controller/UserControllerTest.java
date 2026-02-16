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

	private UserController userController;
	private User validUser;
	private static final String USER_LOGIN = "dolore";
	private static final String USER_NAME = "Nick Name";
	private static final String USER_EMAIL = "mail@mail.ru";

	@BeforeEach
	void setUp() {
		//создаем хранилище и сервис до создания контроллерв
		UserService userService = new UserService(new InMemoryUserStorage());
		userController = new UserController(userService);
		validUser = new User();
		validUser.setEmail(USER_EMAIL);
		validUser.setLogin(USER_LOGIN);
		validUser.setName(USER_NAME);
		validUser.setBirthday(LocalDate.of(1990, 8, 20));
	}

	@Test
	void createUser_WithValidData_ReturnsCreatedUser() {
		User result = userController.createUser(validUser);
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
		validUser.setEmail("invalidemail.ru");
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
		validUser.setLogin("test login");
		assertThrows(ValidationException.class, () -> userController.createUser(validUser));
	}

	@Test
	void createUser_WithEmptyNamePositiveTest() {
		validUser.setName("");
		User result = userController.createUser(validUser);
		assertEquals(USER_LOGIN, result.getName());
	}

	@Test
	void createUser_UsesLoginAsNamePositiveTest() {
		validUser.setName(null);
		User result = userController.createUser(validUser);
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
		User result = userController.createUser(validUser);
		assertNotNull(result);
	}

	@Test
	void createUserEmptyRequestNegativeTest() {
		User emptyUser = new User();
		assertThrows(ValidationException.class, () -> userController.createUser(emptyUser));
	}
}
