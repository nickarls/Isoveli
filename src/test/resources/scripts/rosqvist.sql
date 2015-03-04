insert into osoite(id, osoite, postinumero, kaupunki) values (2, 'Trappaksentie 25', '06650', 'Hamari');
insert into perhe(id, nimi, osoite) values (2, 'Rosqvist', 2);
insert into henkilo(id, etunimi, sukunimi, salasana, perhe) values (3, 'Patrik', 'Rosqvist', '9DD4E461268C8034F5C8564E155C67A6', 2);
insert into henkilo(id, etunimi, sukunimi, salasana, perhe) values (4, 'Leo', 'Rosqvist', '9DD4E461268C8034F5C8564E155C67A6', 2);
insert into harrastaja (id, huoltaja, jasennumero, syntynyt, sukupuoli) values (4, 2, '30102005-2', parsedatetime('30.10.2005', 'dd.MM.yyyy'), 'M');
