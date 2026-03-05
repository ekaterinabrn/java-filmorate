package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;


public interface UserStorage {

	User addUser(User user);

	User updateUser(User user);

	void deleteUser(Integer id);

	User getUserById(Integer id);

	Optional<User> findUserById(Integer id);

	List<User> getAllUsers();

	void addFriend(Integer userId, Integer friendId);

	void removeFriend(Integer userId, Integer friendId);

	List<User> getFriends(Integer userId);
}
