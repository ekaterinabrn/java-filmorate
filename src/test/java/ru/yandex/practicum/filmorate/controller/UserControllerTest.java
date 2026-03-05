package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
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
	private UserDto validUserDto;

	@BeforeEach
	void setUp() {
		UserService userService = new UserService(new InMemoryUserStorage());
		userController = new UserController(userService, new UserMapper());
		validUserDto = new UserDto();
		validUserDto.setEmail(USER_EMAIL);
		validUserDto.setLogin(USER_LOGIN);
		validUserDto.setName(USER_NAME);
		validUserDto.setBirthday(USER_BIRTHDAY);
	}

	@Test
	void createUser_WithValidData_ReturnsCreatedUser() {
		UserDto result = userController.createUser(validUserDto).getBody();
		assertNotNull(result);
		assertNotNull(result.getId());
		assertEquals(USER_EMAIL, result.getEmail());
	}

	@Test
	void createUser_EmptyEmailNegativeTest() {
		validUserDto.setEmail("");
		assertThrows(ValidationException.class, () -> userController.createUser(validUserDto));
	}

	@Test
	void createUser_NullEmailNegativeTest() {
		validUserDto.setEmail(null);
		assertThrows(ValidationException.class, () -> userController.createUser(validUserDto));
	}

	@Test
	void createUser_EmailWithoutAtSymbolNegativeTest() {
		validUserDto.setEmail(EMAIL_INVALID_NO_AT);
		assertThrows(ValidationException.class, () -> userController.createUser(validUserDto));
	}

	@Test
	void createUser_EmptyLoginNegativeTest() {
		validUserDto.setLogin("");
		assertThrows(ValidationException.class, () -> userController.createUser(validUserDto));
	}

	@Test
	void createUser_NullLoginNegativeTest() {
		validUserDto.setLogin(null);
		assertThrows(ValidationException.class, () -> userController.createUser(validUserDto));
	}

	@Test
	void createUser_LoginContainingSpacesNegativeTest() {
		validUserDto.setLogin(LOGIN_WITH_SPACES);
		assertThrows(ValidationException.class, () -> userController.createUser(validUserDto));
	}

	@Test
	void createUser_WithEmptyNamePositiveTest() {
		validUserDto.setName("");
		UserDto result = userController.createUser(validUserDto).getBody();
		assertEquals(USER_LOGIN, result.getName());
	}

	@Test
	void createUser_UsesLoginAsNamePositiveTest() {
		validUserDto.setName(null);
		UserDto result = userController.createUser(validUserDto).getBody();
		assertEquals(USER_LOGIN, result.getName());
	}

	@Test
	void createUser_FutureBirthdayNegativeTest() {
		validUserDto.setBirthday(LocalDate.now().plusDays(1));
		assertThrows(ValidationException.class, () -> userController.createUser(validUserDto));
	}

	@Test
	void createUser_TodayBirthdayPositiveTest() {
		validUserDto.setBirthday(LocalDate.now());
		UserDto result = userController.createUser(validUserDto).getBody();
		assertNotNull(result);
	}

	@Test
	void createUserEmptyRequestNegativeTest() {
		UserDto emptyUserDto = new UserDto();
		assertThrows(ValidationException.class, () -> userController.createUser(emptyUserDto));
	}
}
