package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//сервис для работы с пользователями

@Slf4j
@Service
public class UserService {
	private final UserStorage userStorage;


	@Autowired
	public UserService(UserStorage userStorage) {
		this.userStorage = userStorage;
	}

	//создать нового пользователя

	public User createUser(User user) {
		validateUser(user);
		if (user.getName() == null || user.getName().isBlank()) {
			user.setName(user.getLogin());
		}
		return userStorage.addUser(user);
	}

	//обновить существующего пользователя

	public User updateUser(User user) {
		validateUser(user);
		if (user.getId() == null || userStorage.getUserById(user.getId()) == null) {
			throw new NotFoundException("Пользователь с указанным id не найден");
		}
		if (user.getName() == null || user.getName().isBlank()) {
			user.setName(user.getLogin());
		}
		return userStorage.updateUser(user);
	}


	public User getUserById(Integer id) {
		User user = userStorage.getUserById(id);
		if (user == null) {
			throw new NotFoundException("Пользователь с id " + id + " не найден");
		}
		return user;
	}

	// список всех пользователей

	public List<User> getAllUsers() {
		return userStorage.getAllUsers();
	}

	//добавить пользователя в друзья другому пользователю

	public void addFriend(Integer userId, Integer friendId) {
		User user = getUserById(userId);
		User friend = getUserById(friendId);

		user.getFriends().add(friendId.longValue());
		friend.getFriends().add(userId.longValue());

		log.info("Пользователь с id {} добавил в друзья пользователя с id {}", userId, friendId);
	}

	//удалить пользователя из друзей другого пользователя

	public void removeFriend(Integer userId, Integer friendId) {
		User user = getUserById(userId);
		User friend = getUserById(friendId);

		user.getFriends().remove(friendId.longValue());
		friend.getFriends().remove(userId.longValue());

		log.info("Пользователь с id {} удалил из друзей пользователя с id {}", userId, friendId);
	}

	//список друзей пользователя

	public List<User> getFriends(Integer userId) {
		User user = getUserById(userId);
		List<User> friends = new ArrayList<>();
		for (Long friendId : user.getFriends()) {
			friends.add(getUserById(friendId.intValue()));
		}
		return friends;
	}

	//список общих друзей двух пользователей

	public List<User> getCommonFriends(Integer userId, Integer otherId) {
		User user = getUserById(userId);
		User other = getUserById(otherId);

		Set<Long> userFriends = user.getFriends();
		Set<Long> otherFriends = other.getFriends();

		// находим пересечение множеств друзей
		Set<Long> commonFriendIds = userFriends.stream()
				.filter(otherFriends::contains)
				.collect(Collectors.toSet());

		List<User> commonFriends = new ArrayList<>();
		for (Long friendId : commonFriendIds) {
			commonFriends.add(getUserById(friendId.intValue()));
		}
		return commonFriends;
	}

	//валидация данных пользователя

	private void validateUser(User user) {
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
