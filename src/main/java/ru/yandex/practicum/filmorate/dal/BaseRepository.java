package ru.yandex.practicum.filmorate.dal;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * Базовый репозиторий для работы с БД через JdbcTemplate.
 * Содержит общую логику  insert, update, delete.
 */
public abstract class BaseRepository<T> {

	protected final JdbcTemplate jdbc;
	protected final RowMapper<T> mapper;

	protected BaseRepository(JdbcTemplate jdbc, RowMapper<T> mapper) {
		this.jdbc = jdbc;
		this.mapper = mapper;
	}

	protected Optional<T> findOne(String query, Object... params) {
		try {
			T result = jdbc.queryForObject(query, mapper, params);
			return Optional.ofNullable(result);
		} catch (EmptyResultDataAccessException ignored) {
			return Optional.empty();
		}
	}

	protected List<T> findMany(String query, Object... params) {
		return jdbc.query(query, mapper, params);
	}

	protected long insert(String query, Object... params) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbc.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			for (int i = 0; i < params.length; i++) {
				ps.setObject(i + 1, params[i]);
			}
			return ps;
		}, keyHolder);
		Number key = keyHolder.getKey();
		if (key != null) {
			return key.longValue();
		}
		throw new IllegalStateException("Не удалось сохранить данные:  ключ отсутствует");
	}

	protected void update(String query, Object... params) {
		int rowsUpdated = jdbc.update(query, params);
		if (rowsUpdated == 0) {
			throw new IllegalStateException("Не удалось обновить данные");
		}
	}

	protected boolean delete(String query, Object... params) {
		int rowsDeleted = jdbc.update(query, params);
		return rowsDeleted > 0;
	}
}
