insert into osoite(id, osoite, postinumero, kaupunki) values (1, 'Vaakunatie 10 as 7', '20780', 'Kaarina');
insert into perhe(id, nimi, osoite) values (1, 'Karlsson', 1);
insert into henkilo(id, etunimi, sukunimi, salasana, perhe) values (1, 'Nicklas', 'Karlsson', '9DD4E461268C8034F5C8564E155C67A6', 1);
insert into henkilo(id, etunimi, sukunimi, salasana, perhe) values (2, 'Emil', 'Karlsson', '9DD4E461268C8034F5C8564E155C67A6', 1);
insert into harrastaja (id, huoltaja, jasennumero, syntynyt, sukupuoli) values (2, 1, '30102005-1', parsedatetime('30.10.2005', 'dd.MM.yyyy'), 'M');
