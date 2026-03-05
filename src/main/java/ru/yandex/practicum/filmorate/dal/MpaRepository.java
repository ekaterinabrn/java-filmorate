package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaRepository {

	private static final String FIND_ALL_QUERY = "SELECT mpa_id, code AS name FROM mpa_ratings ORDER BY mpa_id";
	private static final String FIND_BY_ID_QUERY = "SELECT mpa_id, code AS name FROM mpa_ratings WHERE mpa_id = ?";

	private final JdbcTemplate jdbc;

	public MpaRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public List<Mpa> findAll() {
		return jdbc.query(FIND_ALL_QUERY, (rs, rowNum) ->
				new Mpa(rs.getInt("MPA_ID"), rs.getString("NAME")));
	}

	public Optional<Mpa> findById(Integer id) {
		List<Mpa> list = jdbc.query(FIND_BY_ID_QUERY, (rs, rowNum) ->
				new Mpa(rs.getInt("MPA_ID"), rs.getString("NAME")), id);
		return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
	}
}
