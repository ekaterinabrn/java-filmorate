package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
		log.info("Получен запрос на создание пользователя с логином: {}, именем: {}", user.getLogin(), user.getName());
		User createdUser = userService.createUser(user);
		log.info("Пользователь создан id: {}", createdUser.getId());
		return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
	}


	@PutMapping
	public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
		log.info("Получен запрос на обновление пользователя с id: {}", user.getId());
		User updatedUser = userService.updateUser(user);
		log.info("Пользователь с id {} успешно обновлен", updatedUser.getId());
		return ResponseEntity.ok(updatedUser);
	}

	// список всех пользователей

	@GetMapping
	public ResponseEntity<List<User>> getAllUsers() {
		log.info("Получен запрос на получение списка всех пользователей");
		return ResponseEntity.ok(userService.getAllUsers());
	}

	//пользователь по его идентификатору

	@GetMapping("/{id}")
	public ResponseEntity<User> getUserById(@PathVariable Integer id) {
		log.info("Получен запрос на получение пользователя с id: {}", id);
		return ResponseEntity.ok(userService.getUserById(id));
	}

	//добавить пользователя в друзья другому пользователю

	@PutMapping("/{id}/friends/{friendId}")
	public ResponseEntity<Void> addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
		log.info("Получен запрос на добавление в друзья: пользователь {} добавляет пользователя {}", id, friendId);
		userService.addFriend(id, friendId);
		return ResponseEntity.ok().build();
	}


	@DeleteMapping("/{id}/friends/{friendId}")
	public ResponseEntity<Void> removeFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
		log.info("Получен запрос на удаление из друзей: пользователь {} удаляет пользователя {}", id, friendId);
		userService.removeFriend(id, friendId);
		return ResponseEntity.ok().build();
	}

	//список друзей пользователя

	@GetMapping("/{id}/friends")
	public ResponseEntity<List<User>> getFriends(@PathVariable Integer id) {
		log.info("Получен запрос на получение списка друзей пользователя с id: {}", id);
		return ResponseEntity.ok(userService.getFriends(id));
	}

	//список общих друзей пользователей

	@GetMapping("/{id}/friends/common/{otherId}")
	public ResponseEntity<List<User>> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
		log.info("Получен запрос на получение общих друзей пользователей {} и {}", id, otherId);
		return ResponseEntity.ok(userService.getCommonFriends(id, otherId));
	}
}