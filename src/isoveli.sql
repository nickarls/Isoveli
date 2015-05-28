// LOKI
drop table if exists loki;
create table loki(
	id int not null auto_increment,
	koska datetime not null default current_timestamp,
	kuka varchar(50) not null,
	mita varchar(1000) not null
);

// ASETUKSET
drop table if exists asetukset;
create table asetukset(
	installaatio varchar(50) not null,
	saaja varchar(50),
	iban varchar(50),
	bic varchar(50),
	viitenumero number,
	osoite varchar(50),
	postinumero varchar(10),
	kaupunki varchar(50),
	ytunnus varchar(10),
	alvtunnus varchar(10),
	puhelin varchar(20),
	sahkoposti varchar(50),
	kotisivut varchar(50),
	viivastysprosentti number,
	maksuaikaa number,
	ylaotsikko varchar(20),
	huomio varchar(300),
	ghostscript varchar(100),
	tulostin varchar(100),
	constraint pk_asetukset primary key(installaatio)
);
insert into asetukset(installaatio, saaja, iban, bic, viitenumero, osoite, postinumero, kaupunki, ytunnus, alvtunnus, puhelin, sahkoposti, kotisivut, viivastysprosentti, maksuaikaa, huomio, ylaotsikko, ghostscript, tulostin) values ('Budokwai', 'Budokwai ry Taekwondo', 'OKOYFIHH', 'FI04 5711 6140 0501 84', 1, 'Kirstinkatu 1', '20200', 'Turku', '1012368-8', 'FI10123688', '0405960298', 'laskut@budokwai.fi', 'www.budokwai.fi/taekwondo', 5, 14, 'K√ÑYTT√ÑK√Ñ√Ñ AINA\nVIITENUMEROA MAKSAESSANNE', 'LASKU', 'c:/Program Files/gs/gs9.15/bin/gswin64c.exe', '\\KUUTTI\TKU2-RATA-4250');

// OSOITE
drop table if exists osoite;
create table osoite(
	id int not null auto_increment,
	osoite varchar(200) not null,
	postinumero varchar(10) not null,
	kaupunki varchar(50) not null,
	constraint pk_osoite primary key(id)
);
insert into osoite(id, osoite, postinumero, kaupunki) values (1, 'Vaakunatie 10 as 7', '20780', 'Kaarina');
insert into osoite(id, osoite, postinumero, kaupunki) values (2, 'Diket 1', '20010', 'Kyrksl√§pp');

// YHTEYSTIETO
drop table if exists yhteystieto;
create table yhteystieto(
	id int not null auto_increment,
	puhelinnumero varchar(20),
	sahkoposti varchar(50),
	sahkopostilistalla varchar(1) default 'K',
	constraint pk_yhteystieto primary key(id)
);
insert into yhteystieto(id, puhelinnumero, sahkoposti) values (1, '0405062266', 'nickarls@gmail.com');
insert into yhteystieto(id, puhelinnumero, sahkoposti) values (2, '0407218809', 'heidi.karlsson@abo.fi');
insert into yhteystieto(id, puhelinnumero, sahkoposti) values (3, '0507211234', 'patrik.rosqvist@iki.fi');


// PERHE
drop table if exists perhe;
create table perhe
(
	id int not null auto_increment,
	nimi varchar(50) not null,
	osoite int not null,
	constraint pk_perhe primary key(id),
	constraint perhe_osoite_viittaus foreign key(osoite) references osoite(id)	
);
insert into perhe(id, nimi, osoite) values (1, 'Karlsson', 1);
insert into perhe(id, nimi, osoite) values (2, 'Rosqvist', 2);

// BLOBDATA
drop table if exists blobdata;
create table blobdata(
	id int not null auto_increment,
	nimi varchar(20) not null,
	tyyppi int not null,
	tieto blob,
	avain varchar(100),
	vakiomateriaali varchar(1) default 'E' not null,
	constraint pk_blobdata primary key(id),
	constraint uniikki_blobnimi unique(nimi),
	constraint uniikki_blobavain unique(avain)	
);

// HENKIL÷ñ
drop table if exists henkilo;
create table henkilo(
	id int not null auto_increment,
	etunimi varchar(50) not null,
	sukunimi varchar(50) not null,
	osoite int,
	yhteystiedot int,
	perhe int,
	salasana varchar(50),
	kuva int,
	paperilasku varchar(1) default 'E' not null,
	arkistoitu varchar(1) default 'E' not null,
	luotu date not null default current_timestamp,
	constraint pk_henkilo primary key(id),
	constraint henkilo_osoite_viittaus foreign key(osoite) references osoite(id),	
	constraint henkilo_perhe_viittaus foreign key(perhe) references perhe(id),	
	constraint henkilo_kuva_viittaus foreign key(kuva) references blobdata(id),	
	constraint henkilo_yhteystieto_viittaus foreign key(yhteystiedot) references yhteystieto(id),	
);
insert into henkilo(id, etunimi, sukunimi, yhteystiedot, salasana, perhe) values (1, 'Nicklas', 'Karlsson', 1, '9DD4E461268C8034F5C8564E155C67A6', 1);
insert into henkilo(id, etunimi, sukunimi, yhteystiedot, salasana, perhe) values (2, 'Heidi', 'Karlsson', 2, '9DD4E461268C8034F5C8564E155C67A6', 1);
insert into henkilo(id, etunimi, sukunimi, yhteystiedot, salasana, perhe) values (3, 'Patrik', 'Rosqvist', 3, '9DD4E461268C8034F5C8564E155C67A6', 2);
insert into henkilo(id, etunimi, sukunimi, salasana, perhe) values (4, 'Vilmer', 'Rosqvist', '9DD4E461268C8034F5C8564E155C67A6', 2);
insert into henkilo(id, etunimi, sukunimi, salasana, perhe) values (5, 'Leo', 'Rosqvist', '9DD4E461268C8034F5C8564E155C67A6', 2);
insert into henkilo(id, etunimi, sukunimi, salasana, perhe) values (6, 'Karolina', 'Rosqvist', '9DD4E461268C8034F5C8564E155C67A6', 2);
insert into henkilo(id, etunimi, sukunimi, salasana) values (7, 'Erkki', 'Sepp√§', '9DD4E461268C8034F5C8564E155C67A6');
insert into henkilo(id, etunimi, sukunimi, salasana, perhe) values (8, 'Emil', 'Karlsson', '9DD4E461268C8034F5C8564E155C67A6', 1);
insert into henkilo(id, etunimi, sukunimi, salasana, perhe) values (9, 'Anton', 'Karlsson', '9DD4E461268C8034F5C8564E155C67A6', 1);

// HARRASTAJA
drop table if exists harrastaja;
create table harrastaja(
	id int not null auto_increment,
	huoltaja int,
	lisenssinumero varchar(20),
	jasennumero varchar(20),
	syntynyt date not null,
	sukupuoli varchar(1) not null,
	ice varchar(50),
	huomautus varchar(1000),
	taukoalkaa date,
	taukopaattyy date,
	infotiskille varchar(1) not null default 'E',
	siirtotreeneja int default 0,
	medialupa varchar(1) not null default 'K',
	koulutus varchar(4000),
	tulokset varchar(4000),
	constraint pk_harrastaja primary key(id),
	constraint uniikki_jasennumero unique(jasennumero),
	constraint uniikki_lisenssinumero unique(lisenssinumero),
	constraint harrastaja_huoltaja_viittaus foreign key(huoltaja) references henkilo(id)
);
insert into harrastaja (id, jasennumero, lisenssinumero, syntynyt, sukupuoli) values (1, '28061975-1', '666', parsedatetime('28.06.1975', 'dd.MM.yyyy'), 'M');
insert into harrastaja (id, jasennumero, lisenssinumero, syntynyt, sukupuoli) values (2, '09081976-1', '667', parsedatetime('09.08.1976', 'dd.MM.yyyy'), 'N');
insert into harrastaja (id, huoltaja, jasennumero, syntynyt, sukupuoli) values (4, 3, '01052003-1', parsedatetime('01.05.2003', 'dd.MM.yyyy'), 'M');
insert into harrastaja (id, huoltaja, jasennumero, syntynyt, sukupuoli) values (5, 3, '01052003-2', parsedatetime('01.05.2003', 'dd.MM.yyyy'), 'M');
insert into harrastaja (id, huoltaja, jasennumero, syntynyt, sukupuoli) values (6, 3, '05052005-1', parsedatetime('05.05.2005', 'dd.MM.yyyy'), 'N');
insert into harrastaja (id, huoltaja, jasennumero, syntynyt, sukupuoli) values (8, 1, '30102005-1', parsedatetime('30.10.2005', 'dd.MM.yyyy'), 'M');
insert into harrastaja (id, huoltaja, jasennumero, syntynyt, sukupuoli) values (9, 1, '22082003-1', parsedatetime('22.08.2003', 'dd.MM.yyyy'), 'M');

// ROOLI
drop table if exists rooli;
create table rooli(
	id int not null auto_increment,
	nimi varchar(20) not null,
	arkistoitu varchar(1) not null default 'E',	
	constraint pk_rooli primary key(id),
	constraint uniikki_roolinimi unique(nimi)
);
insert into rooli(id, nimi) values (1, 'Yll‰pit‰j‰');
insert into rooli(id, nimi) values (2, 'Treenien vet‰j‰');
insert into rooli(id, nimi) values (3, 'P‰ivyst‰j‰');
insert into rooli(id, nimi) values (4, 'Vyˆkokeen pit‰j‰');

// HENKILOROOLI
drop table if exists henkilorooli;
create table henkilorooli(
	henkilo int not null,
	rooli int not null,
	constraint uniikki_henkilorooli unique(henkilo, rooli),
	constraint henkilorooli_harrastaja_viittaus foreign key(henkilo) references henkilo(id),
	constraint henkilorooli_rooli_viittaus foreign key(rooli) references rooli(id)
);
insert into henkilorooli(henkilo, rooli) values (1, 1);
insert into henkilorooli(henkilo, rooli) values (1, 2);
insert into henkilorooli(henkilo, rooli) values (1, 3);
insert into henkilorooli(henkilo, rooli) values (1, 4);

// VYOARVO
drop table if exists vyoarvo;
create table vyoarvo(
	id int not null auto_increment,
	nimi varchar(10) not null,
	kuvaus varchar(20) not null,
	minimikuukaudet int,
	minimitreenit int,
	jarjestys int,
	poom varchar(1) not null default 'E',
	dan varchar(1) not null default 'E',
	arkistoitu varchar(1) not null default 'E',
	constraint pk_vyoarvo primary key(id),
	constraint uniikki_vyoarvo_nimi unique(nimi),
	constraint uniikki_vyoarvo_kuvaus unique(kuvaus),
	constraint uniikki_jarjestys unique(jarjestys)
);
insert into vyoarvo (id, nimi, kuvaus, minimikuukaudet, minimitreenit, jarjestys) values (1, '8.kup', 'Keltainen', 2, 15, 1);
insert into vyoarvo (id, nimi, kuvaus, minimikuukaudet, minimitreenit, jarjestys) values (2, '7.kup', 'Keltainen+natsa', 3, 25, 2);
insert into vyoarvo (id, nimi, kuvaus, minimikuukaudet, minimitreenit, jarjestys) values (3, '6.kup', 'Vihre√§', 3, 25, 3);
insert into vyoarvo (id, nimi, kuvaus, minimikuukaudet, minimitreenit, jarjestys) values (4, '5.kup', 'Vihre√§+natsa', 3, 25, 4);
insert into vyoarvo (id, nimi, kuvaus, minimikuukaudet, minimitreenit, jarjestys) values (5, '4.kup', 'Sininen', 4, 35, 5);
insert into vyoarvo (id, nimi, kuvaus, minimikuukaudet, minimitreenit, jarjestys) values (6, '3.kup', 'Sininen+natsa', 4, 35, 6);
insert into vyoarvo (id, nimi, kuvaus, minimikuukaudet, minimitreenit, jarjestys) values (7, '2.kup', 'Punainen', 5, 40, 7);
insert into vyoarvo (id, nimi, kuvaus, minimikuukaudet, minimitreenit, jarjestys) values (8, '1.kup', 'Punainen+natsa', 6, 50, 8);
insert into vyoarvo (id, nimi, kuvaus, minimikuukaudet, minimitreenit, jarjestys, poom) values (9, '1.poom', 'Poom I', 6, 60, 9, 'K');
insert into vyoarvo (id, nimi, kuvaus, minimikuukaudet, minimitreenit, jarjestys, dan) values (10, '1.dan', 'Dan I', 6, 60, 10, 'K');
insert into vyoarvo (id, nimi, kuvaus, minimikuukaudet, minimitreenit, jarjestys, poom) values (11, '2.poom', 'Poom II', 24, 200, 11, 'K');
insert into vyoarvo (id, nimi, kuvaus, minimikuukaudet, minimitreenit, jarjestys, dan) values (12, '2.dan', 'Dan II', 24, 200, 12, 'K');
insert into vyoarvo (id, nimi, kuvaus, minimikuukaudet, minimitreenit, jarjestys, poom) values (13, '3.poom', 'Poom III', 36, 300, 13, 'K');
insert into vyoarvo (id, nimi, kuvaus, minimikuukaudet, minimitreenit, jarjestys, dan) values (14, '3.dan', 'Dan III', 36, 300, 14, 'K');
insert into vyoarvo (id, nimi, kuvaus, minimikuukaudet, minimitreenit, jarjestys, poom) values (15, '4.poom', 'Poom IV', 48, 400, 15, 'K');
insert into vyoarvo (id, nimi, kuvaus, minimikuukaudet, minimitreenit, jarjestys, dan) values (16, '4.dan', 'Dan IV', 48, 400, 16, 'K');
insert into vyoarvo (id, nimi, kuvaus, minimikuukaudet, minimitreenit, jarjestys, dan) values (17, '5.dan', 'Dan V', 60, 500, 17, 'K');
insert into vyoarvo (id, nimi, kuvaus, minimikuukaudet, minimitreenit, jarjestys, dan) values (18, '6.dan', 'Dan VI', 72, 600, 18, 'K');

// VYOKOE
drop table if exists vyokoe;
create table vyokoe(
	id int not null auto_increment,
	harrastaja int not null,
	vyoarvo int not null,
	paiva date not null,
	constraint pk_vyokoe primary key(id),
	constraint uniikki_vyokoe unique(harrastaja, vyoarvo),
	constraint vyokoe_harrastaja_viittaus foreign key(harrastaja) references harrastaja(id),
	constraint vyokoe_vyoarvo_viittaus foreign key(vyoarvo) references vyoarvo(id)
);
insert into vyokoe(id, harrastaja, vyoarvo, paiva) values (1, 1, 9, parsedatetime('12.12.2006', 'dd.MM.yyyy'));
insert into vyokoe(id, harrastaja, vyoarvo, paiva) values (2, 2, 1, parsedatetime('12.12.2005', 'dd.MM.yyyy'));

// KISATYYPPI
drop table if exists kisatyyppi;
create table kisatyyppi(
	id int not null auto_increment,
	nimi varchar(50) not null,
	constraint pk_kisatyyppi primary key(id),
	constraint uniikki_kisatyyppinimi unique(nimi)
);
insert into kisatyyppi(id, nimi) values (1, 'Poomsae');
insert into kisatyyppi(id, nimi) values (2, 'Kjorugi');

// KISATULOS
drop table if exists kisatulos;
create table kisatulos(
	id int not null auto_increment,
	harrastaja int not null,
	tyyppi int not null,
	paiva date not null,
	kisa varchar(50) not null,
	sarja varchar(50),
	tulos varchar(10) not null,
	constraint pk_kisatulos primary key(id),
	constraint kisatulos_harrastaja_viittaus foreign key(harrastaja) references harrastaja(id)
);
insert into kisatulos(id, harrastaja, tyyppi, paiva, kisa, sarja, tulos) values (1, 1, 1, parsedatetime('12.12.2006', 'dd.MM.yyyy'), 'Masters Cup', 'Seniorit', '1');

// SOPIMUSTYYPPI
drop table if exists sopimustyyppi;
create table sopimustyyppi(
	id int not null auto_increment,
	nimi varchar(50) not null,
	jasenmaksu varchar(1) not null default 'E',
	harjoittelumaksu varchar(1) not null default 'E',
	treenikertoja varchar(1) not null default 'E',
	alkeiskurssi varchar(1) not null default 'E',
	koeaika varchar(1) not null default 'E',
	vapautus varchar(1) not null default 'E',
	power varchar(1) not null default 'E',
	opiskelija varchar(1) not null default 'E',
	valmennuskeskus varchar(1) not null default 'E',
	laskutettava varchar(1) not null default 'E',
	oletuskuukaudetvoimassa int default 0,
	oletusmaksuvali int default 0,
	oletustreenikerrat int default 0,
	hinta double not null default 0,
	alaikaraja int not null default 0,
	ylaikaraja int not null default 0,
	arkistoitu varchar(1) not null default 'E',
	constraint pk_sopimustyyppi primary key(id),
	constraint uniikki_sopimusnimi unique(nimi)
);

insert into sopimustyyppi(id, nimi, jasenmaksu, laskutettava, hinta) values (1, 'J√§senmaksu', 'K', 'K', 10);
insert into sopimustyyppi(id, nimi, harjoittelumaksu, oletusmaksuvali, laskutettava, hinta, alaikaraja, ylaikaraja) values (2, 'Harjoittelu (4-6v)', 'K', 3, 'K', 19, 4, 6);
insert into sopimustyyppi(id, nimi, harjoittelumaksu, oletusmaksuvali, laskutettava, hinta, alaikaraja, ylaikaraja) values (3, 'Harjoittelu (7-17v)', 'K', 3, 'K', 29, 7, 17);
insert into sopimustyyppi(id, nimi, harjoittelumaksu, oletusmaksuvali, laskutettava, hinta, alaikaraja) values (4, 'Harjoittelu (18+v)', 'K', 3, 'K', 39, 18);
insert into sopimustyyppi(id, nimi, harjoittelumaksu, oletusmaksuvali, laskutettava, hinta, opiskelija) values (5, 'Harjoittelu (opisk.)', 'K', 3, 'K', 29, 'K');
insert into sopimustyyppi(id, nimi, treenikertoja, harjoittelumaksu, oletustreenikerrat, laskutettava, hinta) values (6, 'Kertamaksuja', 'K', 'K', 10, 'K', 6);
insert into sopimustyyppi(id, nimi, alkeiskurssi, harjoittelumaksu, jasenmaksu, oletuskuukaudetvoimassa, laskutettava, hinta, alaikaraja, ylaikaraja) values (7, 'Alkeiskurssi (7-15v/opisk)', 'K', 'K', 'K', 3, 'K', 149, 7, 15);
insert into sopimustyyppi(id, nimi, alkeiskurssi, harjoittelumaksu, jasenmaksu, oletuskuukaudetvoimassa, laskutettava, hinta, alaikaraja) values (8, 'Alkeiskurssi (16+)', 'K', 'K', 'K', 3, 'K', 179, 16);
insert into sopimustyyppi(id, nimi, jasenmaksu, harjoittelumaksu, koeaika, oletustreenikerrat, oletuskuukaudetvoimassa) values (9, 'Koeaika', 'K', 'K', 'K', 2, 1);
insert into sopimustyyppi(id, nimi, harjoittelumaksu, vapautus) values (10, 'Vapautus', 'K', 'K');
insert into sopimustyyppi(id, nimi, jasenmaksu, harjoittelumaksu, power) values (11, 'Power', 'K', 'K', 'K');
insert into sopimustyyppi(id, nimi, jasenmaksu, harjoittelumaksu, valmennuskeskus) values (12, 'Valmennuskeskus', 'K', 'K', 'K');

// LASKU
drop table if exists lasku;
create table lasku(
	id int not null auto_increment,
	henkilo int not null,
	tila varchar(1) not null default 'A',
	muodostettu datetime not null default current_timestamp,
	lahetetty datetime,
	erapaiva date,
	maksettu date,
	pdf int,
	laskutettu varchar(1) not null default 'E',
	viitenumero varchar(50),
	constraint pk_lasku primary key(id),
	constraint lasku_henkilo_viittaus foreign key(henkilo) references henkilo(id),
	constraint lasku_pdf_viittaus foreign key(pdf) references blobdata(id)
);
insert into lasku(id, henkilo, erapaiva, maksettu, tila, laskutettu, viitenumero) values (1, 2, parsedatetime('14.1.2013', 'dd.MM.yyyy'), parsedatetime('10.1.2013', 'dd.MM.yyyy'), 'M', 'K', 12345);

drop table if exists laskurivi;
create table laskurivi(
	id int not null auto_increment,
	lasku int not null,
	rivinumero int not null,
	sopimuslasku int,
	tuotenimi varchar(50) not null,
	infotieto varchar(200),
	maara int not null default 1,
	yksikko varchar(20) not null default 'kpl',
	yksikkohinta double not null default 0,
	luotu datetime not null default current_timestamp,	
	constraint pk_laskurivi primary key(id),
	constraint uniikki_sopimuslasku unique(sopimuslasku),
	constraint uniikki_laskurivi unique(lasku, rivinumero),
	constraint laskurivi_lasku_viittaus foreign key(lasku) references lasku(id)
);
insert into laskurivi(id, lasku, rivinumero, sopimuslasku, tuotenimi, infotieto, maara, yksikko, yksikkohinta, luotu) values (1, 1, 1, 1, 'Harjoittelumaksu (Heidi)', '1.1.2012-1.6.2012', 1, 'kpl', 60.00, parsedatetime('14.1.2013', 'dd.MM.yyyy'));

// SOPIMUS
drop table if exists sopimus;
create table sopimus(
	id int not null auto_increment,
	harrastaja int not null,
	tyyppi int not null,
	luotu date not null default current_date,
	umpeutuu date,
	treenikertojatilattu int default 0,
	treenikertojajaljella int default 0,
	maksuvali int default 12,
	arkistoitu varchar(1) not null default 'E',
	siirtomaksuvoimassa date,
	constraint pk_sopimus primary key(id),
	constraint sopimus_harrastaja_viittaus foreign key(harrastaja) references harrastaja(id),
	constraint sopimus_tyyppi_viittaus foreign key(tyyppi) references sopimustyyppi(id)
);

//insert into sopimus(id, harrastaja, tyyppi, luotu) values (1, 1, 1, parsedatetime('02.01.2015', 'dd.MM.yyyy')); 					// Nicklas, j√§senmaksu
//insert into sopimus(id, harrastaja, tyyppi, luotu) values (2, 1, 6, parsedatetime('03.01.2015', 'dd.MM.yyyy')); 					// Nicklas, vapautus
//insert into sopimus(id, harrastaja, tyyppi, luotu) values (7, 2, 1, parsedatetime('04.01.2015', 'dd.MM.yyyy')); 					// Heidi, j√§senmaksu
insert into sopimus(id, harrastaja, tyyppi, luotu) values (3, 2, 2, parsedatetime('05.01.2015', 'dd.MM.yyyy'));						// Heidi, harjoittelumaksu 
//insert into sopimus(id, harrastaja, tyyppi, luotu) values (4, 4, 1, parsedatetime('06.01.2015', 'dd.MM.yyyy')); 					// Vilmer, j√§senmaksu
//insert into sopimus(id, harrastaja, tyyppi, treenikertoja, luotu) values (6, 4, 3, 0, parsedatetime('07.01.2015', 'dd.MM.yyyy'));	// Vilmer, kymppikerta 
//insert into sopimus(id, harrastaja, tyyppi, luotu) values (5, 5, 1, parsedatetime('08.01.2015', 'dd.MM.yyyy')); 					// Leo, alkeiskurssi
//insert into sopimus(id, harrastaja, tyyppi, luotu) values (8, 5, 4, parsedatetime('09.01.2015', 'dd.MM.yyyy')); 					// Leo, j√§senmaksu
//insert into sopimus(id, harrastaja, tyyppi, treenikertoja, luotu) values (9, 6, 5, 3, parsedatetime('10.01.2015', 'dd.MM.yyyy')); 	// Karolina, koekertoja

// Sopimuslasku
drop table if exists sopimuslasku;
create table sopimuslasku (
	id int not null auto_increment,
	sopimus int not null,
	alkaa date,
	paattyy date,
	laskurivi int,
	luotu datetime not null default current_timestamp,	
	constraint pk_sopimuslasku primary key(id),	
	constraint sopimuslasku_sopimus_viittaus foreign key(sopimus) references sopimus(id),
	constraint sopimuslasku_laskurivi_viittaus foreign key(laskurivi) references laskurivi(id)
);

insert into sopimuslasku(id, sopimus, alkaa, paattyy, laskurivi) values (1, 3, parsedatetime('1.1.2013', 'dd.MM.yyyy'), parsedatetime('30.5.2013', 'dd.MM.yyyy'), 1);

alter table laskurivi add constraint laskurivi_sopimuslasku_viittaus foreign key(sopimuslasku) references sopimuslasku(id);

// TREENITYYPPI
drop table if exists treenityyppi;
create table treenityyppi(
	id int not null auto_increment,
	nimi varchar(50) not null,
	arkistoitu varchar(1) not null default 'E',
	constraint pk_treenityyppi primary key(id),
	constraint uniikki_treenityyppi unique (nimi),
);
insert into treenityyppi(id, nimi) values (1, 'Yleinen');
insert into treenityyppi(id, nimi) values (2, 'Perustekniikkaa');
insert into treenityyppi(id, nimi) values (3, 'Ottelu');
insert into treenityyppi(id, nimi) values (4, 'Alkeiskurssi');
insert into treenityyppi(id, nimi) values (5, 'HIIT');
insert into treenityyppi(id, nimi) values (6, 'Balance');
insert into treenityyppi(id, nimi) values (7, 'Itsepuolustus');

// TREENI
drop table if exists treeni;
create table treeni(
	id int not null auto_increment,
	nimi varchar(100) not null,
	sijainti varchar(100),
	paiva int not null,
	alkaa time not null,
	paattyy time not null,
	tyyppi int,
	valmennuskeskus varchar(1) not null default 'E',
	power varchar(1) default 'E' not null,
	voimassaalkaa date,
	voimassapaattyy date,
	vyoalaraja int,
	vyoylaraja int,
	ikaalaraja int,
	ikaylaraja int,
	arkistoitu varchar(1) not null default 'E',	
	constraint pk_treeni primary key(id),
	constraint uniikki_treeni unique (nimi, paiva),
	constraint treeni_tyyppi_viittaus foreign key(tyyppi) references treenityyppi(id),
	constraint vyoarvo_alaraja_viittaus foreign key(vyoalaraja) references vyoarvo(id),
	constraint vyoarvo_ylaraja_viittaus foreign key(vyoylaraja) references vyoarvo(id)	
);
insert into treeni(id, nimi, paiva, alkaa, paattyy, tyyppi) values (1, 'reeni', 2, parsedatetime('15:00', 'HH:mm'), parsedatetime('16:00', 'HH:mm'), 1);
insert into treeni(id, nimi, paiva, alkaa, paattyy, tyyppi) values (2, 'toinen reeni', 2, parsedatetime('16:00', 'HH:mm'), parsedatetime('17:30', 'HH:mm'), 1);

// TREENIVETAJA
drop table if exists treenivetaja;
create table treenivetaja(
	id int not null auto_increment,
	harrastaja int not null,
	treeni int not null,
	constraint pk_treenivetaja primary key(id),
	constraint uniikki_treenivetaja unique (harrastaja, treeni),
	constraint treenivetaja_harrastaja_viittaus foreign key(harrastaja) references harrastaja(id),
	constraint treenivetaja_treeni_viittaus foreign key(treeni) references treeni(id)
);
insert into treenivetaja(id, harrastaja, treeni) values (1, 1, 1);

// TREENISESSIO
drop table if exists treenisessio;
create table treenisessio(
	id int not null auto_increment,
	treeni int not null,
	paiva date not null,
	constraint pk_treenisessio primary key(id),
	constraint uniikki_treenisessio unique (treeni, paiva),
	constraint treenisessio_treeni_viittaus foreign key(treeni) references treeni(id)
);
insert into treenisessio(id, treeni, paiva) values (1, 1, parsedatetime('12.12.2012', 'dd.MM.yyyy'));

// TREENIKAYNTI
drop table if exists treenikaynti;
create table treenikaynti(
	id int not null auto_increment,
	aikaleima datetime not null,
	treenisessio int not null,
	harrastaja int not null,
	constraint pk_treenikaynti primary key(id),
	constraint treenikaynti_harrastaja_viittaus foreign key(harrastaja) references harrastaja(id),
	constraint treenikaynti_treenisessio_viittaus foreign key(treenisessio) references treenisessio(id),
	constraint uniikki_treenikaynti unique(harrastaja, treenisessio)
);
insert into treenikaynti(id, aikaleima, treenisessio, harrastaja) values (1, parsedatetime('12.12.2012 12:12:12', 'dd.MM.yyyy HH:mm:ss'), 1, 1);

// TREENISESSIOVETAJA
drop table if exists treenisessiovetaja;
create table treenisessiovetaja(
	id int not null auto_increment,
	harrastaja int not null,
	treenisessio int not null,
	constraint pk_treenisessiovetaja primary key(id),
	constraint uniikki_treenisessio_vetaja unique (harrastaja, treenisessio),
	constraint treenisessiovetaja_harrastaja_viittaus foreign key(harrastaja) references harrastaja(id),
	constraint treenisessiovetaja_treenisessio_viittaus foreign key(treenisessio) references treenisessio(id)
);
insert into treenisessiovetaja(id, harrastaja, treenisessio) values (1, 1, 1);

// VY√ñKOETILAISUUS
drop table if exists vyokoetilaisuus;
create table vyokoetilaisuus(
	id int not null auto_increment,
	koska datetime not null,
	vyoalaraja int,
	vyoylaraja int,
	ikaalaraja int,
	ikaylaraja int,
	pitaja int not null,
	constraint pk_vyokoetilaisuus primary key(id),
	constraint vyokoetilaisuus_pitaja_viittaus foreign key(pitaja) references harrastaja(id)
);

insert into vyokoetilaisuus(id, koska, vyoalaraja, vyoylaraja, ikaalaraja, ikaylaraja, pitaja) values (1, parsedatetime('10.06.2015 18:00', 'dd.MM.yyyy HH:mm'), 2, 6, 10, 14, 1);

// VY√ñKOKELAS
drop table if exists vyokokelas;
create table vyokokelas(
	id int not null auto_increment,
	vyokoetilaisuus int not null,
	harrastaja int not null,
	tavoite int not null,
	vetajahyvaksynta varchar(1) not null default 'K',
	maksu varchar(1) not null default 'K',
	passi varchar(1) not null default 'E',
	onnistui varchar(1) not null default 'E',
	constraint pk_vyokokelas primary key(id),
	constraint uniikki_kokelas unique (harrastaja, vyokoetilaisuus),
	constraint vyokokelas_vyoarvo_viittaus foreign key(tavoite) references vyoarvo(id),
	constraint vyokokelas_harrastaja_viittaus foreign key(harrastaja) references harrastaja(id),
	constraint vyokokelas_tilaisuus_viittaus foreign key(vyokoetilaisuus) references vyokoetilaisuus(id)
);

insert into vyokokelas(id, vyokoetilaisuus, harrastaja, tavoite) values (1, 1, 1, 13);

// VIESTI
drop table if exists viesti;
create table viesti(
	id int not null auto_increment,
	otsikko varchar(100) not null,
	sisalto varchar(4000) not null,
	lahettaja int not null,
	vastaanottajat varchar(4000) not null,
	luotu datetime not null,
	constraint pk_viesti primary key(id),
	constraint viesti_lahettaja_viittaus foreign key(lahettaja) references harrastaja(id)
);

insert into viesti(id, otsikko, sisalto, lahettaja, vastaanottajat, luotu) values (1, 'otsikko', 'sisalt√∂', 1, 'Kaikki', parsedatetime('10.06.2015 18:00', 'dd.MM.yyyy HH:mm'));

// VIESTILAATIKKO
drop table if exists viestilaatikko;
create table viestilaatikko (
	id int not null auto_increment,
	omistaja int not null,
	tyyppi varchar(1) not null,
	constraint pk_viestilaatikko primary key(id),
	constraint uniikki_viestilaatikko unique (omistaja, tyyppi),
	constraint henkiloviesti_omistaja_viittaus foreign key(omistaja) references henkilo(id)
);

insert into viestilaatikko (id, omistaja, tyyppi) values (1, 1, 'I');

// HENKIL√ñVIESTI
drop table if exists henkiloviesti;
create table henkiloviesti(
	id int not null auto_increment,
	viesti int not null,
	viestilaatikko int not null,
	luettu varchar(1) not null default 'E',
	arkistoitu varchar(1) not null default 'E',
	constraint pk_henkiloviesti primary key(id),
	constraint uniikki_viesti unique (viesti, viestilaatikko),
	constraint henkiloviesti_viestilaatikko_viittaus foreign key(viestilaatikko) references viestilaatikko(id)
);
	
insert into henkiloviesti(id, viesti, viestilaatikko) values (1, 1, 1);	