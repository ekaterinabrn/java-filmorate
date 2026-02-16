package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
	private final UserService userService;


	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

	//создать нового пользователя

	@PostMapping
	public User createUser(@Valid @RequestBody User user) {
		log.info("Получен запрос на создание пользователя с логином: {}, именем: {}", user.getLogin(), user.getName());
		User createdUser = userService.createUser(user);
		log.info("Пользователь создан id: {}", createdUser.getId());
		return createdUser;
	}


	@PutMapping
	public User updateUser(@Valid @RequestBody User user) {
		log.info("Получен запрос на обновление пользователя с id: {}", user.getId());
		User updatedUser = userService.updateUser(user);
		log.info("Пользователь с id {} успешно обновлен", updatedUser.getId());
		return updatedUser;
	}

	// список всех пользователей

	@GetMapping
	public List<User> getAllUsers() {
		log.info("Получен запрос на получение списка всех пользователей");
		return userService.getAllUsers();
	}

	//пользователь по его идентификатору

	@GetMapping("/{id}")
	public User getUserById(@PathVariable Integer id) {
		log.info("Получен запрос на получение пользователя с id: {}", id);
		return userService.getUserById(id);
	}

	//добавить пользователя в друзья другому пользователю

	@PutMapping("/{id}/friends/{friendId}")
	public void addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
		log.info("Получен запрос на добавление в друзья: пользователь {} добавляет пользователя {}", id, friendId);
		userService.addFriend(id, friendId);
	}


	@DeleteMapping("/{id}/friends/{friendId}")
	public void removeFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
		log.info("Получен запрос на удаление из друзей: пользователь {} удаляет пользователя {}", id, friendId);
		userService.removeFriend(id, friendId);
	}

	//список друзей пользователя

	@GetMapping("/{id}/friends")
	public List<User> getFriends(@PathVariable Integer id) {
		log.info("Получен запрос на получение списка друзей пользователя с id: {}", id);
		return userService.getFriends(id);
	}

	//список общих друзей пользователей

	@GetMapping("/{id}/friends/common/{otherId}")
	public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
		log.info("Получен запрос на получение общих друзей пользователей {} и {}", id, otherId);
		return userService.getCommonFriends(id, otherId);
	}
}