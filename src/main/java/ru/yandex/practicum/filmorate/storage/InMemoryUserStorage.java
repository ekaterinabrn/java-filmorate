package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;


@Component
@org.springframework.beans.factory.annotation.Qualifier("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
	private final Map<Integer, User> users = new HashMap<>();
	private int nextId = 1;


	@Override
	public User addUser(User user) {
		user.setId(nextId++);
		users.put(user.getId(), user);
		return user;
	}


	@Override
	public User updateUser(User user) {
		users.put(user.getId(), user);
		return user;
	}


	@Override
	public void deleteUser(Integer id) {
		users.remove(id);
	}


	@Override
	public User getUserById(Integer id) {
		return users.get(id);
	}

	@Override
	public Optional<User> findUserById(Integer id) {
		return Optional.ofNullable(users.get(id));
	}

	@Override
	public List<User> getAllUsers() {
		return new ArrayList<>(users.values());
	}

	@Override
	public void addFriend(Integer userId, Integer friendId) {
		User user = users.get(userId);
		if (user != null) user.getFriends().add(friendId.longValue());
	}

	@Override
	public void removeFriend(Integer userId, Integer friendId) {
		User user = users.get(userId);
		if (user != null) user.getFriends().remove(friendId.longValue());
	}

	@Override
	public List<User> getFriends(Integer userId) {
		User user = users.get(userId);
		if (user == null) return List.of();
		List<User> friends = new ArrayList<>();
		for (Long fid : user.getFriends()) {
			User f = users.get(fid.intValue());
			if (f != null) friends.add(f);
		}
		return friends;
	}
}
