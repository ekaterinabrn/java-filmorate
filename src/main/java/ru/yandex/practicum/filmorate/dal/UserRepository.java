package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("userDbStorage")
public class UserRepository extends BaseRepository<User> implements UserStorage {

	private static final String FIND_ALL_QUERY = "SELECT USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY FROM users ORDER BY USER_ID";
	private static final String FIND_BY_ID_QUERY = "SELECT USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY FROM users WHERE USER_ID = ?";
	private static final String INSERT_QUERY = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
	private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
	private static final String DELETE_QUERY = "DELETE FROM users WHERE user_id = ?";
	private static final String FRIENDS_IDS_QUERY = "SELECT friend_id FROM friendships WHERE user_id = ?";
	private static final String INSERT_FRIEND_QUERY = "INSERT INTO friendships (user_id, friend_id, status) VALUES (?, ?, 'UNCONFIRMED')";
	private static final String DELETE_FRIEND_QUERY = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
	private static final String FRIENDS_QUERY = "SELECT u.user_id, u.email, u.login, u.name, u.birthday FROM users u " +
			"INNER JOIN friendships f ON u.user_id = f.friend_id WHERE f.user_id = ? ORDER BY u.user_id";

	public UserRepository(JdbcTemplate jdbc, UserRowMapper mapper) {
		super(jdbc, mapper);
	}

	@Override
	public User addUser(User user) {
		long id = insert(INSERT_QUERY, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
		user.setId((int) id);
		return user;
	}

	@Override
	public User updateUser(User user) {
		jdbc.update(UPDATE_QUERY, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
		return getUserById(user.getId());
	}

	@Override
	public void deleteUser(Integer id) {
		delete(DELETE_QUERY, id);
	}

	@Override
	public User getUserById(Integer id) {
		List<User> list = findMany(FIND_BY_ID_QUERY, id);
		if (list.isEmpty()) return null;
		User user = list.get(0);
		loadFriends(user);
		return user;
	}

	@Override
	public Optional<User> findUserById(Integer id) {
		return Optional.ofNullable(getUserById(id));
	}

	@Override
	public List<User> getAllUsers() {
		List<User> list = findMany(FIND_ALL_QUERY);
		list.forEach(this::loadFriends);
		return list;
	}

	@Override
	public void addFriend(Integer userId, Integer friendId) {
		jdbc.update(INSERT_FRIEND_QUERY, userId, friendId);
	}

	@Override
	public void removeFriend(Integer userId, Integer friendId) {
		jdbc.update(DELETE_FRIEND_QUERY, userId, friendId);
	}

	@Override
	public List<User> getFriends(Integer userId) {
		return jdbc.query(FRIENDS_QUERY, mapper, userId);
	}

	private void loadFriends(User user) {
		List<Long> ids = jdbc.query(FRIENDS_IDS_QUERY, (rs, rowNum) -> rs.getLong("FRIEND_ID"), user.getId());
		user.setFriends(new HashSet<>(ids));
	}
}
