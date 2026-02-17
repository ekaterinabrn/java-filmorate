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
	public UserService(UserStorage userStorage) {
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
	 * Добавить пользователя в друзья другому пользователю
	 *
	 * @param userId идентификатор пользователя
	 * @param friendId идентификатор друга
	 */
	public void addFriend(Integer userId, Integer friendId) {
		log.debug("Начинаем добавление в друзья: пользователь {} добавляет пользователя {}", userId, friendId);
		User user = getUserById(userId);
		User friend = getUserById(friendId);
		user.getFriends().add(friendId.longValue());
		friend.getFriends().add(userId.longValue());
	}

	/**
	 * Удалить пользователя из друзей другого пользователя
	 *
	 * @param userId идентификатор пользователя
	 * @param friendId идентификатор друга
	 */
	public void removeFriend(Integer userId, Integer friendId) {
		log.debug("Начинаем удаление из друзей: пользователь {} удаляет пользователя {}", userId, friendId);
		User user = getUserById(userId);
		User friend = getUserById(friendId);
		user.getFriends().remove(friendId.longValue());
		friend.getFriends().remove(userId.longValue());
	}

	/**
	 * Список друзей пользователя
	 *
	 * @param userId идентификатор пользователя
	 * @return список друзей пользователя
	 */
	public List<User> getFriends(Integer userId) {
		log.debug("Получаем список друзей пользователя с id: {}", userId);
		User user = getUserById(userId);
		List<User> friends = new ArrayList<>();
		for (Long friendId : user.getFriends()) {
			friends.add(getUserById(friendId.intValue()));
		}
		return friends;
	}

	/**
	 * Список общих друзей двух пользователей
	 *
	 * @param userId идентификатор первого пользователя
	 * @param otherId идентификатор второго пользователя
	 * @return список общих друзей
	 */
	public List<User> getCommonFriends(Integer userId, Integer otherId) {
		log.debug("Начинаем поиск общих друзей пользователей {} и {}", userId, otherId);
		User user = getUserById(userId);
		User other = getUserById(otherId);

		Set<Long> userFriends = user.getFriends();
		Set<Long> otherFriends = other.getFriends();

		Set<Long> commonFriendIds = userFriends.stream()
				.filter(otherFriends::contains)
				.collect(Collectors.toSet());

		List<User> commonFriends = new ArrayList<>();
		for (Long friendId : commonFriendIds) {
			commonFriends.add(getUserById(friendId.intValue()));
		}
		return commonFriends;
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
