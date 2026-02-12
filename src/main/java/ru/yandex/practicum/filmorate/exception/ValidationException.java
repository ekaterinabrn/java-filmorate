package ru.yandex.practicum.filmorate.exception;

//исключение для обработки ошибок валидации данных

public class ValidationException extends RuntimeException {
	public ValidationException(String message) {
		super(message);
	}
}
