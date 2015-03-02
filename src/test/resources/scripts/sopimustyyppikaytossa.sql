insert into henkilo(id, etunimi, sukunimi, salasana) values (1, 'Nicklas', 'Karlsson', '9DD4E461268C8034F5C8564E155C67A6');
insert into harrastaja (id, jasennumero, syntynyt, sukupuoli) values (1, '19750628-1', parsedatetime('28.06.1975', 'dd.MM.yyyy'), 'M');
insert into sopimustyyppi(id, nimi, jasenmaksu, laskutettava, hinta) values (1, 'Jäsenmaksu', 'K', 'K', 10);
insert into sopimus(id, harrastaja, tyyppi, luotu) values (1, 1, 1, parsedatetime('02.01.2015', 'dd.MM.yyyy'));
