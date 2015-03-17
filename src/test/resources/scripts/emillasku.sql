set referential_integrity false;
insert into lasku(id, henkilo, erapaiva, viitenumero, tila) values (1, 3, parsedatetime('14.1.2013', 'dd.MM.yyyy'), 12345, 'M');
insert into laskurivi(id, lasku, rivinumero, sopimuslasku, tuotenimi, infotieto, maara, yksikko, yksikkohinta, luotu) values (3, 1, 1, 1, 'Harjoittelumaksu (Heidi)', '1.1.2012-1.6.2012', 1, 'kpl', 60.00, parsedatetime('14.1.2013', 'dd.MM.yyyy'));
insert into sopimuslasku(id, sopimus, alkaa, paattyy, laskurivi) values (1, 3, parsedatetime('1.1.2013', 'dd.MM.yyyy'), parsedatetime('30.5.2013', 'dd.MM.yyyy'), 1);
set referential_integrity true;