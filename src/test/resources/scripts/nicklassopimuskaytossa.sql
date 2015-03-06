insert into sopimus(id, harrastaja, tyyppi, luotu) values (1, 1, 4, parsedatetime('02.01.2015', 'dd.MM.yyyy'));
insert into lasku(id, henkilo, erapaiva, maksettu, tila, laskutettu, viitenumero, luotu) values (1, 1, parsedatetime('14.1.2013', 'dd.MM.yyyy'), parsedatetime('10.1.2013', 'dd.MM.yyyy'), 'M', 'K', 12345, parsedatetime('1.1.2013', 'dd.MM.yyyy'));
insert into laskurivi(id, lasku, rivinumero, sopimuslasku, tuotenimi, infotieto, maara, yksikko, yksikkohinta, luotu) values (1, 1, 1, 1, 'Harjoittelumaksu (Heidi)', '1.1.2012-1.6.2012', 1, 'kpl', 60.00, parsedatetime('14.1.2013', 'dd.MM.yyyy'));
insert into sopimuslasku(id, sopimus, alkaa, paattyy, laskurivi) values (1, 1, parsedatetime('1.1.2013', 'dd.MM.yyyy'), parsedatetime('30.5.2013', 'dd.MM.yyyy'), 1);
