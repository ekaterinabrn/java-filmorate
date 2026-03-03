package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserRepository.class, UserRowMapper.class})
class UserRepositoryTest {

	private static final int NONEXISTENT_USER_ID = 1;
	private static final String FIELD_ID = "id";

	private static final String EMAIL_TEST = "test@mail.ru";
	private static final String LOGIN_TEST = "login";
	private static final String NAME = "Jine";
	private static final LocalDate BIRTHDAY_1990 = LocalDate.of(1990, 8, 20);

	private static final String EMAIL_A = "a@b.ru";
	private static final String LOGIN_1 = "login1";

	private static final String EMAIL_OLD = "old@b.ru";
	private static final String LOGIN_OLD = "oldlogin";
	private static final String NAME_OLD = "Old";
	private static final String NAME_NEW = "NewName";
	private static final String EMAIL_NEW = "new@b.ru";
	private static final LocalDate BIRTHDAY_1990_JAN = LocalDate.of(1990, 1, 1);

	private static final String EMAIL_1 = "1@b.ru";
	private static final String LOGIN_LOG1 = "log1";
	private static final String EMAIL_2 = "2@b.ru";
	private static final String LOGIN_LOG2 = "log2";
	private static final LocalDate BIRTHDAY_1995 = LocalDate.of(1995, 1, 1);

	private static final String EMAIL_DELETE = "d@b.ru";
	private static final String LOGIN_DELETE = "dell";

	private static final int EXPECTED_TWO_USERS = 2;
	private static final int EXPECTED_ONE_FRIEND = 1;

	private final UserStorage userStorage;

	@Test
	void findUserById_emptyDb_returnsEmpty() {
		Optional<User> opt = userStorage.findUserById(NONEXISTENT_USER_ID);
		assertThat(opt).isEmpty();
	}

	@Test
	void testFindUserById() {
		User user = new User();
		user.setEmail(EMAIL_TEST);
		user.setLogin(LOGIN_TEST);
		user.setName(NAME);
		user.setBirthday(BIRTHDAY_1990);
		userStorage.addUser(user);
		Optional<User> userOptional = userStorage.findUserById(user.getId());

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(u ->
						assertThat(u).hasFieldOrPropertyWithValue(FIELD_ID, user.getId())
				);
	}

	@Test
	void addUser_and_findUserById_returnsUser() {
		User user = new User();
		user.setEmail(EMAIL_A);
		user.setLogin(LOGIN_1);
		user.setName(NAME);
		user.setBirthday(BIRTHDAY_1990_JAN);
		User created = userStorage.addUser(user);
		assertThat(created.getId()).isNotNull();
		Optional<User> found = userStorage.findUserById(created.getId());
		assertThat(found).isPresent().hasValueSatisfying(u -> {
			assertThat(u.getId()).isEqualTo(created.getId());
			assertThat(u.getEmail()).isEqualTo(EMAIL_A);
			assertThat(u.getLogin()).isEqualTo(LOGIN_1);
		});
	}

	@Test
	void updateUser_updatesInDb() {
		User user = new User();
		user.setEmail(EMAIL_OLD);
		user.setLogin(LOGIN_OLD);
		user.setName(NAME_OLD);
		user.setBirthday(BIRTHDAY_1990_JAN);
		User created = userStorage.addUser(user);
		created.setName(NAME_NEW);
		created.setEmail(EMAIL_NEW);
		User updated = userStorage.updateUser(created);
		assertThat(updated.getName()).isEqualTo(NAME_NEW);
		assertThat(userStorage.getUserById(created.getId()).getName()).isEqualTo(NAME_NEW);
	}

	@Test
	void getAllUsers_returnsAll() {
		User u1 = new User();
		u1.setEmail(EMAIL_1);
		u1.setLogin(LOGIN_LOG1);
		u1.setBirthday(BIRTHDAY_1990_JAN);
		userStorage.addUser(u1);
		User u2 = new User();
		u2.setEmail(EMAIL_2);
		u2.setLogin(LOGIN_LOG2);
		u2.setBirthday(BIRTHDAY_1995);
		userStorage.addUser(u2);
		List<User> all = userStorage.getAllUsers();
		assertThat(all).hasSize(EXPECTED_TWO_USERS);
	}

	@Test
	void addFriend_and_getFriends() {
		User u1 = new User();
		u1.setEmail(EMAIL_1);
		u1.setLogin(LOGIN_LOG1);
		u1.setBirthday(BIRTHDAY_1990_JAN);
		userStorage.addUser(u1);
		User u2 = new User();
		u2.setEmail(EMAIL_2);
		u2.setLogin(LOGIN_LOG2);
		u2.setBirthday(BIRTHDAY_1995);
		userStorage.addUser(u2);
		userStorage.addFriend(u1.getId(), u2.getId());
		List<User> friends = userStorage.getFriends(u1.getId());
		assertThat(friends).hasSize(EXPECTED_ONE_FRIEND);
		assertThat(friends.get(0).getId()).isEqualTo(u2.getId());
	}

	@Test
	void removeFriend_removesFromList() {
		User u1 = new User();
		u1.setEmail(EMAIL_1);
		u1.setLogin(LOGIN_LOG1);
		u1.setBirthday(BIRTHDAY_1990_JAN);
		userStorage.addUser(u1);
		User u2 = new User();
		u2.setEmail(EMAIL_2);
		u2.setLogin(LOGIN_LOG2);
		u2.setBirthday(BIRTHDAY_1995);
		userStorage.addUser(u2);
		userStorage.addFriend(u1.getId(), u2.getId());
		userStorage.removeFriend(u1.getId(), u2.getId());
		List<User> friends = userStorage.getFriends(u1.getId());
		assertThat(friends).isEmpty();
	}

	@Test
	void deleteUser_removesUser() {
		User u = new User();
		u.setEmail(EMAIL_DELETE);
		u.setLogin(LOGIN_DELETE);
		u.setBirthday(BIRTHDAY_1990_JAN);
		userStorage.addUser(u);
		userStorage.deleteUser(u.getId());
		assertThat(userStorage.findUserById(u.getId())).isEmpty();
	}
}
