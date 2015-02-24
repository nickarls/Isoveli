insert into sopimus(id, harrastaja, tyyppi, luotu) values (1, 1, 4, parsedatetime('02.01.2015', 'dd.MM.yyyy'));
update harrastaja set taukoalkaa=parsedatetime('01.02.2015', 'dd.MM.yyyy'), taukopaattyy=parsedatetime('01.03.2015', 'dd.MM.yyyy') where id=1;
