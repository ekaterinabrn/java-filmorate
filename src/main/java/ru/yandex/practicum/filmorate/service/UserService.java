package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

/**
 * Сервис для работы с пользователями
 */
@Slf4j
@Service
public class UserService {
	private static final String VALIDATION_ERROR_PREFIX = "Ошибка валидации: ";
	private static final String USER_NOT_FOUND = "Пользователь с id {} не найден";
	private static final String USER_NOT_FOUND_MESSAGE = "Пользователь с id ";

	private final UserStorage userStorage;


	@Autowired
	public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
		this.userStorage = userStorage;
	}

	/**
	 * Создать нового пользователя
	 *
	 * @param user пользователь для создания
	 * @return созданный пользователь с присвоенным id
	 */
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

	/**
	 * Обновить существующего пользователя
	 *
	 * @param user пользователь для обновления
	 * @return обновленный пользователь
	 */
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

	/**
	 * Получить пользователя по идентификатору
	 *
	 * @param id идентификатор пользователя
	 * @return найденный пользователь
	 */
	public User getUserById(Integer id) {
		User user = userStorage.getUserById(id);
		if (user == null) {
			log.warn(USER_NOT_FOUND, id);
			throw new NotFoundException(USER_NOT_FOUND_MESSAGE + id + " не найден");
		}
		return user;
	}

	/**
	 * Список всех пользователей
	 *
	 * @return список всех пользователей
	 */
	public List<User> getAllUsers() {
		log.debug("Получаем список всех пользователей");
		return userStorage.getAllUsers();
	}

	/**
	 * Добавить пользователя в друзья (односторонняя заявка: friendId попадает в список друзей userId).
	 */
	public void addFriend(Integer userId, Integer friendId) {
		log.debug("Начинаем добавление в друзья: пользователь {} добавляет пользователя {}", userId, friendId);
		getUserById(userId);
		getUserById(friendId);
		userStorage.addFriend(userId, friendId);
	}

	/**
	 * Удалить пользователя из друзей.
	 */
	public void removeFriend(Integer userId, Integer friendId) {
		log.debug("Начинаем удаление из друзей: пользователь {} удаляет пользователя {}", userId, friendId);
		getUserById(userId);
		getUserById(friendId);
		userStorage.removeFriend(userId, friendId);
	}

	/**
	 * Список друзей пользователя.
	 */
	public List<User> getFriends(Integer userId) {
		log.debug("Получаем список друзей пользователя с id: {}", userId);
		getUserById(userId);
		return userStorage.getFriends(userId);
	}

	/**
	 * Список общих друзей двух пользователей.
	 */
	public List<User> getCommonFriends(Integer userId, Integer otherId) {
		log.debug("Начинаем поиск общих друзей пользователей {} и {}", userId, otherId);
		User user = getUserById(userId);
		User other = getUserById(otherId);
		Set<Long> userFriendIds = user.getFriends();
		Set<Long> otherFriendIds = other.getFriends();
		Set<Long> commonIds = userFriendIds.stream().filter(otherFriendIds::contains).collect(Collectors.toSet());
		List<User> result = new ArrayList<>();
		for (Long fid : commonIds) {
			result.add(getUserById(fid.intValue()));
		}
		return result;
	}

	/**
	 * Валидация данных пользователя
	 *
	 * @param user пользователь для валидации
	 */
	private void validateUser(User user) {
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
	}
}
