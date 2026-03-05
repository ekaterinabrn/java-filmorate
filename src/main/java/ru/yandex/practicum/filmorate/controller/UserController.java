package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

	private final UserService userService;
	private final UserMapper userMapper;

	@Autowired
	public UserController(UserService userService, UserMapper userMapper) {
		this.userService = userService;
		this.userMapper = userMapper;
	}

	@PostMapping
	public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
		User user = userMapper.toEntity(userDto);
		User created = userService.createUser(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDto(created));
	}

	@PutMapping
	public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UserDto userDto) {
		User user = userMapper.toEntity(userDto);
		User updated = userService.updateUser(user);
		return ResponseEntity.ok(userMapper.toDto(updated));
	}

	@GetMapping
	public ResponseEntity<List<UserDto>> getAllUsers() {
		return ResponseEntity.ok(
				userService.getAllUsers().stream()
						.map(userMapper::toDto)
						.collect(Collectors.toList()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserDto> getUserById(@PathVariable Integer id) {
		User user = userService.getUserById(id);
		return ResponseEntity.ok(userMapper.toDto(user));
	}

	@PutMapping("/{id}/friends/{friendId}")
	public ResponseEntity<Void> addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
		userService.addFriend(id, friendId);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}/friends/{friendId}")
	public ResponseEntity<Void> removeFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
		userService.removeFriend(id, friendId);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{id}/friends")
	public ResponseEntity<List<UserDto>> getFriends(@PathVariable Integer id) {
		return ResponseEntity.ok(
				userService.getFriends(id).stream()
						.map(userMapper::toDto)
						.collect(Collectors.toList()));
	}

	@GetMapping("/{id}/friends/common/{otherId}")
	public ResponseEntity<List<UserDto>> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
		return ResponseEntity.ok(
				userService.getCommonFriends(id, otherId).stream()
						.map(userMapper::toDto)
						.collect(Collectors.toList()));
	}
}
