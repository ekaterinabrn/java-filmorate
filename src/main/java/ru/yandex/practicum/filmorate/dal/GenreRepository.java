package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreRepository {

	private static final String FIND_ALL_QUERY = "SELECT genre_id, name FROM genres ORDER BY genre_id";
	private static final String FIND_BY_ID_QUERY = "SELECT genre_id, name FROM genres WHERE genre_id = ?";

	private final JdbcTemplate jdbc;

	public GenreRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public List<Genre> findAll() {
		return jdbc.query(FIND_ALL_QUERY, (rs, rowNum) ->
				new Genre(rs.getInt("GENRE_ID"), rs.getString("NAME")));
	}

	public Optional<Genre> findById(Integer id) {
		List<Genre> list = jdbc.query(FIND_BY_ID_QUERY, (rs, rowNum) ->
				new Genre(rs.getInt("GENRE_ID"), rs.getString("NAME")), id);
		return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
	}
}
