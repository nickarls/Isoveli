insert into treenityyppi(id, nimi) values (1, 'Perustekniikkaa');
insert into treeni(id, nimi, paiva, alkaa, paattyy, tyyppi) values (1, 'reeni', 1, parsedatetime('15:00', 'HH:mm'), parsedatetime('16:00', 'HH:mm'), 1);
insert into treenisessio(id, treeni, paiva) values (1, 1, parsedatetime('12.12.2012', 'dd.MM.yyyy'));
insert into treenikaynti(id, aikaleima, treenisessio, harrastaja) values (1, parsedatetime('12.12.2012 12:12:12', 'dd.MM.yyyy HH:mm:ss'), 1, 1);