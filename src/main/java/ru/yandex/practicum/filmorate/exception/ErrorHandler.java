package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;


@Slf4j
@RestControllerAdvice
public class ErrorHandler {
	public static final String VALIDATION_ERROR = "Ошибка валидации";
	public static final String NOT_FOUND_ERROR = "Объект не найден";

	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, String> handleValidationException(ValidationException e) {
		log.error("{}: {}", VALIDATION_ERROR, e.getMessage());
		return Map.of("error", VALIDATION_ERROR, "message", e.getMessage());
	}

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public Map<String, String> handleNotFoundException(NotFoundException e) {
		log.error("{}: {}", NOT_FOUND_ERROR, e.getMessage());
		return Map.of("error", NOT_FOUND_ERROR, "message", e.getMessage());
	}
}
