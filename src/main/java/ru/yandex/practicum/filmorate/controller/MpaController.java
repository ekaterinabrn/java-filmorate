package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MpaController {

	private final MpaRepository mpaRepository;

	@Autowired
	public MpaController(MpaRepository mpaRepository) {
		this.mpaRepository = mpaRepository;
	}

	/**
	 * Список всех рейтингов MPA
	 */
	@GetMapping
	public ResponseEntity<List<Mpa>> getAllMpa() {
		log.info("Получен запрос на получение списка рейтингов MPA");
		return ResponseEntity.ok(mpaRepository.findAll());
	}

	/**
	 * Рейтинг MPA по идентификатору
	 *
	 * @param id идентификатор рейтинга
	 * @return найденный рейтинг
	 */
	@GetMapping("/{id}")
	public ResponseEntity<Mpa> getMpaById(@PathVariable Integer id) {
		log.info("Получен запрос на получение рейтинга MPA с id: {}", id);
		Optional<Mpa> mpaOpt = mpaRepository.findById(id);
		if (mpaOpt.isEmpty()) {
			throw new NotFoundException("Рейтинг MPA с id " + id + " не найден");
		}
		return ResponseEntity.ok(mpaOpt.get());
	}
}
