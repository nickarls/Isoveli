insert into osoite(id, osoite, postinumero, kaupunki) values (1, 'Vaakunatie 10 as 7', '20780', 'Kaarina');
insert into yhteystieto(id, puhelinnumero, sahkoposti) values (1, '0405062266', 'nickarls@gmail.com');
insert into henkilo(id, etunimi, sukunimi, osoite, yhteystiedot, salasana) values (1, 'Nicklas', 'Karlsson', 1, 1, '9DD4E461268C8034F5C8564E155C67A6');
insert into harrastaja (id, jasennumero, syntynyt, sukupuoli, taukoalkaa, taukopaattyy) values (1, '19750628-1', parsedatetime('28.06.1975', 'dd.MM.yyyy'), 'M', parsedatetime('01.01.2015', 'dd.MM.yyyy'), parsedatetime('01.01.2016', 'dd.MM.yyyy'));

