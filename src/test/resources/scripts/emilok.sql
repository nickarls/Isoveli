insert into henkilo(id, etunimi, sukunimi, osoite, yhteystiedot, salasana) values (2, 'Emil', 'Karlsson', 1, 1, '9DD4E461268C8034F5C8564E155C67A6');
insert into harrastaja (id, jasennumero, syntynyt, sukupuoli) values (2, '20051030-1', parsedatetime('30.10.2005', 'dd.MM.yyyy'), 'M');
insert into sopimus(id, harrastaja, tyyppi, luotu) values (3, 2, 1, parsedatetime('02.01.2015', 'dd.MM.yyyy'));
insert into sopimus(id, harrastaja, tyyppi, luotu) values (4, 2, 3, parsedatetime('02.01.2015', 'dd.MM.yyyy'));
