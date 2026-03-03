-- Начальные данные (INSERT с проверкой для идемпотентности)

INSERT INTO mpa_ratings (mpa_id, code, description)
SELECT 1, 'G', 'у фильма нет возрастных ограничений' WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE mpa_id = 1);
INSERT INTO mpa_ratings (mpa_id, code, description)
SELECT 2, 'PG', 'детям рекомендуется смотреть фильм с родителями' WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE mpa_id = 2);
INSERT INTO mpa_ratings (mpa_id, code, description)
SELECT 3, 'PG-13', 'детям до 13 лет просмотр не желателен' WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE mpa_id = 3);
INSERT INTO mpa_ratings (mpa_id, code, description)
SELECT 4, 'R', 'лицам до 17 лет просматривать фильм можно только в присутствии взрослого' WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE mpa_id = 4);
INSERT INTO mpa_ratings (mpa_id, code, description)
SELECT 5, 'NC-17', 'лицам до 18 лет просмотр запрещён' WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE mpa_id = 5);

INSERT INTO genres (genre_id, name)
SELECT 1, 'Комедия' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_id = 1);
INSERT INTO genres (genre_id, name)
SELECT 2, 'Драма' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_id = 2);
INSERT INTO genres (genre_id, name)
SELECT 3, 'Мультфильм' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_id = 3);
INSERT INTO genres (genre_id, name)
SELECT 4, 'Триллер' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_id = 4);
INSERT INTO genres (genre_id, name)
SELECT 5, 'Документальный' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_id = 5);
INSERT INTO genres (genre_id, name)
SELECT 6, 'Боевик' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_id = 6);
