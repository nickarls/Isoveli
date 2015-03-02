insert into henkilo(id, etunimi, sukunimi, salasana) values (1, 'Nicklas', 'Karlsson', '9DD4E461268C8034F5C8564E155C67A6');
insert into harrastaja (id, huoltaja, jasennumero, syntynyt, sukupuoli) values (1, 1, '19750628-1', parsedatetime('28.06.1975', 'dd.MM.yyyy'), 'M');
insert into vyoarvo (id, nimi, kuvaus, minimikuukaudet, minimitreenit, jarjestys) values (1, '8.kup', 'Keltainen', 2, 15, 1);
insert into vyokoe(id, harrastaja, vyoarvo, paiva) values (1, 1, 1, parsedatetime('12.12.2006', 'dd.MM.yyyy'));
