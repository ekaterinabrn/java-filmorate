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
	public static final String INTERNAL_ERROR = "Внутренняя ошибка сервера";

	/**
	 * Возвращает код 400 (Bad Request)
	 *
	 * @param e исключение валидации
	 * @return ответ с ошибкой валидации
	 */
	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, String> handleValidationException(ValidationException e) {
		log.error("{}: {}", VALIDATION_ERROR, e.getMessage());
		return Map.of("error", VALIDATION_ERROR, "message", e.getMessage());
	}

	/**
	 * Возвращает код 404 (Not Found)
	 *
	 * @param e исключение "объект не найден"
	 * @return ответ с ошибкой "объект не найден"
	 */
	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public Map<String, String> handleNotFoundException(NotFoundException e) {
		log.error("{}: {}", NOT_FOUND_ERROR, e.getMessage());
		return Map.of("error", NOT_FOUND_ERROR, "message", e.getMessage());
	}

	/**
	 * Возвращает код 500 (Internal Server Error)
	 *
	 * @param e исключение
	 * @return ответ с внутренней ошибкой сервера
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public Map<String, String> handleException(Exception e) {
		log.error("{}: {}", INTERNAL_ERROR, e.getMessage(), e);
		return Map.of("error", INTERNAL_ERROR, "message", e.getMessage());
	}
}
