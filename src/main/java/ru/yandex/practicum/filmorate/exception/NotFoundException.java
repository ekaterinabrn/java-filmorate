package ru.yandex.practicum.filmorate.exception;

//исключение для обработки ошибок когда объект не найден

public class NotFoundException extends RuntimeException {
	public NotFoundException(String message) {
		super(message);
	}
}
