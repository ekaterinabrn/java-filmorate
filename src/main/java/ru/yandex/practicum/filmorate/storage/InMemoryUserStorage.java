package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
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
	public List<User> getAllUsers() {
		return new ArrayList<>(users.values());
	}
}
