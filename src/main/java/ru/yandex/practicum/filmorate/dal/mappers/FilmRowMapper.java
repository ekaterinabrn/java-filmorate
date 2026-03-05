package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Component
public class FilmRowMapper implements RowMapper<Film> {

	@Override
	public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
		Film film = new Film();
		film.setId(rs.getInt("FILM_ID"));
		film.setName(rs.getString("NAME"));
		film.setDescription(rs.getString("DESCRIPTION"));
		film.setReleaseDate(rs.getObject("RELEASE_DATE", LocalDate.class));
		film.setDuration(rs.getInt("DURATION"));
		film.setMpaId(rs.getObject("MPA_ID", Integer.class));
		return film;
	}
}
