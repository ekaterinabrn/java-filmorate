package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class FilmorateApplicationTests {

	@Test
	void contextLoads() {
		// Проверяем, что класс FilmorateApplication может быть загружен
		FilmorateApplication app = new FilmorateApplication();
		assertNotNull(app);
	}

}
