package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
	private final Map<Integer, User> users = new HashMap<>();
	private int nextId = 1;

	//создание нового пользователя
	@PostMapping
	public User createUser(@Valid @RequestBody User user) {
		log.info("Получен запрос на создание пользователя с логином: {}, именем: {}", user.getLogin(), user.getName());
		validateUser(user);
		// если имя пустое, используем логин
		if (user.getName() == null || user.getName().isBlank()) {
			user.setName(user.getLogin());
		}
		user.setId(nextId++);
		users.put(user.getId(), user);
		log.info("Пользователь  создан  id: {}", user.getId());
		return user;
	}

	//обновление существующего пользователя
	@PutMapping
	public User updateUser(@Valid @RequestBody User user) {
		log.info("Получен запрос на обновление пользователя с id: {}", user.getId());
		validateUser(user);
		if (user.getId() == null || !users.containsKey(user.getId())) {
			log.error("Пользователь с id {} не найден", user.getId());
			throw new ValidationException("Пользователь с указанным id не найден");
		}
		// если имя пустое, используем логин
		if (user.getName() == null || user.getName().isBlank()) {
			user.setName(user.getLogin());
		}
		users.put(user.getId(), user);
		log.info("Пользователь с id {} успешно обновлен", user.getId());
		return user;
	}

	//получение списка всех пользователей
	@GetMapping
	public List<User> getAllUsers() {
		log.info("Получен запрос на получение всех пользователей. Текущее количество: {}", users.size());
		return new ArrayList<>(users.values());
	}

	//проверка email, логин и даты рождения
	void validateUser(User user) {
		if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
			log.error("Ошибка валидации: email не может быть пустым и должен содержать символ @");
			throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
		}
		if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
			log.error("Ошибка валидации: логин не может быть пустым и содержать пробелы");
			throw new ValidationException("Логин не может быть пустым и содержать пробелы");
		}
		if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
			log.error("Ошибка валидации: дата рождения не может быть в будущем");
			throw new ValidationException("Дата рождения не может быть в будущем");
		}
	}
}
