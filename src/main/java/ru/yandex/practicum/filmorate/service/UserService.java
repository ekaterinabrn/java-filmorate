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
	private static final String VALIDATION_ERROR_PREFIX = "Ошибка валидации: ";
	private static final String USER_NOT_FOUND = "Пользователь с id {} не найден";
	private static final String USER_NOT_FOUND_MESSAGE = "Пользователь с id ";
	private static final String USER_ADDED_FRIEND = "Пользователь с id {} добавил в друзья пользователя с id {}";
	private static final String USER_REMOVED_FRIEND = "Пользователь с id {} удалил из друзей пользователя с id {}";

	private final UserStorage userStorage;


	@Autowired
	public UserService(UserStorage userStorage) {
		this.userStorage = userStorage;
	}

	//создать нового пользователя

	public User createUser(User user) {
		log.debug("Начинаем создание пользователя с логином: {}", user.getLogin());
		validateUser(user);
		if (user.getName() == null || user.getName().isBlank()) {
			log.trace("Имя пользователя пустое, используем логин: {}", user.getLogin());
			user.setName(user.getLogin());
		}
		User createdUser = userStorage.addUser(user);
		log.debug("Пользователь успешно создан с id: {}", createdUser.getId());
		return createdUser;
	}

	//обновить существующего пользователя

	public User updateUser(User user) {
		log.debug("Начинаем обновление пользователя с id: {}", user.getId());
		validateUser(user);
		if (user.getId() == null || userStorage.getUserById(user.getId()) == null) {
			log.warn("Попытка обновить несуществующего пользователя с id: {}", user.getId());
			throw new NotFoundException("Пользователь с указанным id не найден");
		}
		if (user.getName() == null || user.getName().isBlank()) {
			log.trace("Имя пользователя пустое, используем логин: {}", user.getLogin());
			user.setName(user.getLogin());
		}
		User updatedUser = userStorage.updateUser(user);
		log.debug("Пользователь с id {} успешно обновлен", updatedUser.getId());
		return updatedUser;
	}


	public User getUserById(Integer id) {
		log.trace("Получаем пользователя с id: {}", id);
		User user = userStorage.getUserById(id);
		if (user == null) {
			log.warn(USER_NOT_FOUND, id);
			throw new NotFoundException(USER_NOT_FOUND_MESSAGE + id + " не найден");
		}
		log.trace("Пользователь с id {} найден: {}", id, user.getLogin());
		return user;
	}

	// список всех пользователей

	public List<User> getAllUsers() {
		log.debug("Получаем список всех пользователей");
		List<User> users = userStorage.getAllUsers();
		log.trace("Найдено пользователей: {}", users.size());
		return users;
	}

	//добавить пользователя в друзья другому пользователю

	public void addFriend(Integer userId, Integer friendId) {
		log.debug("Начинаем добавление в друзья: пользователь {} добавляет пользователя {}", userId, friendId);
		User user = getUserById(userId);
		User friend = getUserById(friendId);

		log.trace("Текущее количество друзей у пользователя {}: {}", userId, user.getFriends().size());
		log.trace("Текущее количество друзей у пользователя {}: {}", friendId, friend.getFriends().size());

		user.getFriends().add(friendId.longValue());
		friend.getFriends().add(userId.longValue());

		log.info(USER_ADDED_FRIEND, userId, friendId);
		log.trace("Количество друзей после добавления у пользователя {}: {}", userId, user.getFriends().size());
		log.trace("Количество друзей после добавления у пользователя {}: {}", friendId, friend.getFriends().size());
	}

	//удалить пользователя из друзей другого пользователя

	public void removeFriend(Integer userId, Integer friendId) {
		log.debug("Начинаем удаление из друзей: пользователь {} удаляет пользователя {}", userId, friendId);
		User user = getUserById(userId);
		User friend = getUserById(friendId);

		log.trace("Текущее количество друзей у пользователя {}: {}", userId, user.getFriends().size());
		log.trace("Текущее количество друзей у пользователя {}: {}", friendId, friend.getFriends().size());

		user.getFriends().remove(friendId.longValue());
		friend.getFriends().remove(userId.longValue());

		log.info(USER_REMOVED_FRIEND, userId, friendId);
		log.trace("Количество друзей после удаления у пользователя {}: {}", userId, user.getFriends().size());
		log.trace("Количество друзей после удаления у пользователя {}: {}", friendId, friend.getFriends().size());
	}

	//список друзей пользователя

	public List<User> getFriends(Integer userId) {
		log.debug("Получаем список друзей пользователя с id: {}", userId);
		User user = getUserById(userId);
		log.trace("У пользователя {} найдено друзей: {}", userId, user.getFriends().size());
		List<User> friends = new ArrayList<>();
		for (Long friendId : user.getFriends()) {
			log.trace("Обрабатываем друга с id: {}", friendId);
			friends.add(getUserById(friendId.intValue()));
		}
		log.debug("Список друзей пользователя {} успешно получен, количество: {}", userId, friends.size());
		return friends;
	}

	//список общих друзей двух пользователей

	public List<User> getCommonFriends(Integer userId, Integer otherId) {
		log.debug("Начинаем поиск общих друзей пользователей {} и {}", userId, otherId);
		User user = getUserById(userId);
		User other = getUserById(otherId);

		Set<Long> userFriends = user.getFriends();
		Set<Long> otherFriends = other.getFriends();
		log.trace("У пользователя {} друзей: {}, у пользователя {} друзей: {}",
				userId, userFriends.size(), otherId, otherFriends.size());

		// находим пересечение множеств друзей
		Set<Long> commonFriendIds = userFriends.stream()
				.filter(otherFriends::contains)
				.collect(Collectors.toSet());

		log.trace("Найдено общих друзей: {}", commonFriendIds.size());

		List<User> commonFriends = new ArrayList<>();
		for (Long friendId : commonFriendIds) {
			log.trace("Обрабатываем общего друга с id: {}", friendId);
			commonFriends.add(getUserById(friendId.intValue()));
		}
		log.debug("Список общих друзей успешно получен, количество: {}", commonFriends.size());
		return commonFriends;
	}

	//валидация данных пользователя

	private void validateUser(User user) {
		log.trace("Проверка валидации пользователя с логином: {}", user.getLogin());
		if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
			log.error(VALIDATION_ERROR_PREFIX + "email не может быть пустым и должен содержать символ @");
			throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
		}
		if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
			log.error(VALIDATION_ERROR_PREFIX + "логин не может быть пустым и содержать пробелы");
			throw new ValidationException("Логин не может быть пустым и содержать пробелы");
		}
		if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
			log.error(VALIDATION_ERROR_PREFIX + "дата рождения не может быть в будущем");
			throw new ValidationException("Дата рождения не может быть в будущем");
		}
		log.trace("Валидация пользователя пройдена успешно");
	}
}
