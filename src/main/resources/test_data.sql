MERGE INTO users (id, email, login, name, birthday)
    VALUES (101, 'a@ya.ru', 'l1', 'n1', '1990-07-27'),
           (102, 'b@ya.ru', 'l2', 'n2', '2990-07-27'),
           (103, 'c@ya.ru', 'l3', 'n3', '3990-07-27');

MERGE INTO films (id, name, description, duration, release_date, mpa_id)
    VALUES (101, 'n1', 'd1', 100, '2000-01-01', 1),
           (102, 'n2', 'd2', 200, '3000-01-01', 2),
           (103, 'n3', 'd3', 300, '4000-01-01', 3);