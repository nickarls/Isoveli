insert into osoite(id, osoite, postinumero, kaupunki) values (1, 'Vaakunatie 10 as 7', '20780', 'Kaarina');
insert into yhteystieto(id, puhelinnumero, sahkoposti) values (1, '0405062266', 'nickarls@gmail.com');
insert into henkilo(id, etunimi, sukunimi, osoite, yhteystiedot, salasana) values (1, 'Nicklas', 'Karlsson', 1, 1, '9DD4E461268C8034F5C8564E155C67A6');
insert into harrastaja (id, jasennumero, syntynyt, sukupuoli) values (1, '19750628-1', parsedatetime('28.06.1975', 'dd.MM.yyyy'), 'M');
insert into sopimus(id, harrastaja, tyyppi, luotu) values (1, 1, 1, parsedatetime('02.01.2015', 'dd.MM.yyyy'));
insert into sopimus(id, harrastaja, tyyppi, luotu) values (2, 1, 4, parsedatetime('02.01.2015', 'dd.MM.yyyy'));
