insert into viesti(id, otsikko, sisalto, lahettaja, vastaanottajat, luotu) values (1, 'otsikko', 'sisaltö', 1, 'Kaikki', parsedatetime('10.06.2015 18:00', 'dd.MM.yyyy HH:mm'));
insert into viestilaatikko (id, omistaja, tyyppi) values (1, 1, 'I');
insert into henkiloviesti(id, viesti, viestilaatikko) values (1, 1, 1);