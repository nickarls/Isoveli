=== LUONTISCRIPTIT ===
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
insert into osoite(id, osoite, postinumero, kaupunki) values (2, 'Diket 1', '20010', 'Kyrksl�pp');

// YHTEYSTIETO
drop table if exists yhteystieto;
create table yhteystieto(
	id int not null auto_increment,
	puhelinnumero varchar(20),
	sahkoposti varchar(30),
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


drop table if exists blobdata;
create table blobdata(
	id int not null auto_increment,
	nimi varchar(20) not null,
	tyyppi int not null,
	tieto blob,
	avain varchar(100) not null,
	constraint pk_blobdata primary key(id),
	constraint uniikki_blobnimi unique(nimi),
	constraint uniikki_blobavain unique(avain)	
);

// HENKIL�
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

// HARRASTAJA
drop table if exists harrastaja;
create table harrastaja(
	id int not null auto_increment,
	huoltaja int,
	jasennumero varchar(10),
	korttinumero varchar(100),
	lisenssinumero varchar(10),
	syntynyt date not null,
	sukupuoli varchar(1) not null,
	ice varchar(50),
	huomautus varchar(1000),
	constraint pk_harrastaja primary key(id),
	constraint uniikki_kortti unique(korttinumero),
	constraint uniikki_jasennumero unique(jasennumero),
	constraint uniikki_lisenssinumero unique(lisenssinumero),
	constraint harrastaja_huoltaja_viittaus foreign key(huoltaja) references henkilo(id),
);
insert into harrastaja (id, jasennumero, korttinumero, lisenssinumero, syntynyt, sukupuoli) values (1, '666', '123', '666', parsedatetime('28.06.1975', 'dd.MM.yyyy'), 'M');
insert into harrastaja (id, jasennumero, korttinumero, lisenssinumero, syntynyt, sukupuoli) values (2, '667', '124', '667', parsedatetime('09.08.1976', 'dd.MM.yyyy'), 'N');
insert into harrastaja (id, huoltaja, jasennumero, korttinumero, lisenssinumero, syntynyt, sukupuoli) values (4, 3, '668', '125', '668', parsedatetime('01.05.2003', 'dd.MM.yyyy'), 'M');
insert into harrastaja (id, huoltaja, jasennumero, korttinumero, lisenssinumero, syntynyt, sukupuoli) values (5, 3, '669', '126', '669', parsedatetime('05.05.2005', 'dd.MM.yyyy'), 'M');
insert into harrastaja (id, huoltaja, jasennumero, korttinumero, lisenssinumero, syntynyt, sukupuoli) values (6, 3, '670', '127', '670', parsedatetime('05.05.2010', 'dd.MM.yyyy'), 'M');

// ROOLI
drop table if exists rooli;
create table rooli(
	id int not null auto_increment,
	nimi varchar(20) not null,
	constraint pk_rooli primary key(id),
	constraint uniikki_roolinimi unique(nimi)
);
insert into rooli(id, nimi) values (1, 'Yll�pit�j�');
insert into rooli(id, nimi) values (2, 'Treenien vet�j�');

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

// VYOARVO
drop table if exists vyoarvo;
create table vyoarvo(
	id int not null auto_increment,
	nimi varchar(10) not null,
	minimikuukaudet int,
	minimitreenit int,
	jarjestys int,
	constraint pk_vyoarvo primary key(id),
	constraint uniikki_vyoarvo_nimi unique(nimi),
	constraint uniikki_jarjestys unique(jarjestys)
);
insert into vyoarvo (id, nimi, minimikuukaudet, minimitreenit, jarjestys) values (1, '8.kup', 2, 15, 1);
insert into vyoarvo (id, nimi, minimikuukaudet, minimitreenit, jarjestys) values (2, '7.kup', 3, 25, 2);
insert into vyoarvo (id, nimi, minimikuukaudet, minimitreenit, jarjestys) values (3, '6.kup', 3, 25, 3);
insert into vyoarvo (id, nimi, minimikuukaudet, minimitreenit, jarjestys) values (4, '5.kup', 3, 25, 4);
insert into vyoarvo (id, nimi, minimikuukaudet, minimitreenit, jarjestys) values (5, '4.kup', 4, 35, 5);
insert into vyoarvo (id, nimi, minimikuukaudet, minimitreenit, jarjestys) values (6, '3.kup', 4, 35, 6);
insert into vyoarvo (id, nimi, minimikuukaudet, minimitreenit, jarjestys) values (7, '2.kup', 5, 40, 7);
insert into vyoarvo (id, nimi, minimikuukaudet, minimitreenit, jarjestys) values (8, '1.kup', 6, 50, 8);
insert into vyoarvo (id, nimi, minimikuukaudet, minimitreenit, jarjestys) values (9, '1.dan', 6, 60, 9);
insert into vyoarvo (id, nimi, minimikuukaudet, minimitreenit, jarjestys) values (10, '2.dan', 24, 200, 10);
insert into vyoarvo (id, nimi, minimikuukaudet, minimitreenit, jarjestys) values (11, '3.dan', 36, 300, 11);

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
	perhealennus varchar(1) not null default 'E',
	laskutettava varchar(1) not null default 'E',
	oletuskuukaudetvoimassa int default 0,
	oletusmaksuvali int default 0,
	oletustreenikerrat int default 0,
	hinta double not null default 0,
	tuotekoodi varchar(20),
	maara int not null default 1,
	yksikko varchar(20) not null default 'kpl',
	verokanta int not null default 0,
	constraint pk_sopimustyyppi primary key(id),
	constraint uniikki_sopimusnimi unique(nimi)
);
insert into sopimustyyppi(id, nimi, jasenmaksu, laskutettava) values (1, 'J�senmaksu', 'K', 'K');
insert into sopimustyyppi(id, nimi, harjoittelumaksu, oletusmaksuvali, laskutettava) values (2, 'Harjoittelumaksu', 'K', 12, 'K');
insert into sopimustyyppi(id, nimi, treenikertoja, harjoittelumaksu, oletustreenikerrat, laskutettava) values (3, 'Kymppikerta', 'K', 'K', 10, 'K');
insert into sopimustyyppi(id, nimi, alkeiskurssi, jasenmaksu, harjoittelumaksu, oletuskuukaudetvoimassa, laskutettava) values (4, 'Alkeiskurssi', 'K', 'K', 'K', 3, 'K');
insert into sopimustyyppi(id, nimi, koeaika, jasenmaksu, harjoittelumaksu) values (5, 'Koeaika', 'K', 'K', 'K');
insert into sopimustyyppi(id, nimi, vapautus) values (6, 'Vapautus', 'K');
insert into sopimustyyppi(id, nimi, power) values (7, 'Power', 'K');
insert into sopimustyyppi(id, nimi, perhealennus) values (8, 'Perhealennus', 'K');

// LASKU
drop table if exists lasku;
create table lasku(
	id int not null auto_increment,
	henkilo int not null,
	tila varchar(1) not null default 'A',
	erapaiva date,
	maksettu date,
	luotu datetime not null default current_timestamp,
	pdf int,
	laskutettu varchar(1) not null default 'E',
	constraint pk_lasku primary key(id),
	constraint lasku_henkilo_viittaus foreign key(henkilo) references henkilo(id),
	constraint lasku_pdf_viittaus foreign key(pdf) references blobdata(id)
);
insert into lasku(id, henkilo, erapaiva, maksettu, tila, laskutettu) values (1, 2, parsedatetime('14.1.2014', 'dd.MM.yyyy'), parsedatetime('10.1.2014', 'dd.MM.yyyy'), 'M', 'K');

drop table if exists laskurivi;
create table laskurivi(
	id int not null auto_increment,
	lasku int not null,
	rivinumero int not null,
	sopimuslasku int,
	luotu datetime not null default current_timestamp,	
	constraint pk_laskurivi primary key(id),
	constraint uniikki_laskurivinumero unique(lasku, rivinumero),
	constraint uniikki_sopimuslasku unique(sopimuslasku),
	constraint laskurivi_lasku_viittaus foreign key(lasku) references lasku(id)
);
insert into laskurivi(id, lasku, rivinumero, sopimuslasku) values (1, 1, 1, 1);

// SOPIMUS
drop table if exists sopimus;
create table sopimus(
	id int not null auto_increment,
	harrastaja int not null,
	tyyppi int not null,
	umpeutuu date,
	treenikertoja int default 0,
	maksuvali int default 12,
	constraint pk_sopimus primary key(id),
	constraint sopimus_harrastaja_viittaus foreign key(harrastaja) references harrastaja(id),
	constraint sopimus_tyyppi_viittaus foreign key(tyyppi) references sopimustyyppi(id)
);

insert into sopimus(id, harrastaja, tyyppi, umpeutuu, maksuvali) values (1, 1, 1, parsedatetime('31.12.2014', 'dd.MM.yyyy'), 6); 
insert into sopimus(id, harrastaja, tyyppi) values (2, 1, 6); 
insert into sopimus(id, harrastaja, tyyppi, umpeutuu) values (3, 2, 2, parsedatetime('31.12.2014', 'dd.MM.yyyy')); 
insert into sopimus(id, harrastaja, tyyppi, umpeutuu) values (4, 4, 1, parsedatetime('31.12.2014', 'dd.MM.yyyy')); 
insert into sopimus(id, harrastaja, tyyppi, umpeutuu) values (5, 5, 1, parsedatetime('31.12.2014', 'dd.MM.yyyy')); 
insert into sopimus(id, harrastaja, tyyppi, treenikertoja) values (6, 4, 3, 0); 
insert into sopimus(id, harrastaja, tyyppi) values (7, 2, 1); 
insert into sopimus(id, harrastaja, tyyppi, umpeutuu) values (8, 5, 4, parsedatetime('30.11.2014', 'dd.MM.yyyy')); 
insert into sopimus(id, harrastaja, tyyppi, treenikertoja) values (9, 6, 5, 3); 

// Sopimuslasku
drop table if exists sopimuslasku;
create table sopimuslasku (
	id int not null auto_increment,
	sopimus int not null,
	alkaa date,
	paattyy date,
	laskurivi int,
	constraint pk_sopimuslasku primary key(id),	
	constraint sopimuslasku_sopimus_viittaus foreign key(sopimus) references sopimus(id),
	constraint sopimuslasku_laskurivi_viittaus foreign key(laskurivi) references laskurivi(id)
);

insert into sopimuslasku(id, sopimus, alkaa, paattyy, laskurivi) values (1, 3, parsedatetime('1.1.2014', 'dd.MM.yyyy'), parsedatetime('30.5.2014', 'dd.MM.yyyy'), 1);

alter table laskurivi add constraint laskurivi_sopimuslasku_viittaus foreign key(sopimuslasku) references sopimuslasku(id);

// TREENITYYPPI
drop table if exists treenityyppi;
create table treenityyppi(
	id int not null auto_increment,
	nimi varchar(50) not null,
	constraint pk_treenityyppi primary key(id),
	constraint uniikki_treenityyppi unique (nimi),
);
insert into treenityyppi(id, nimi) values (1, 'Perustekniikkaa');

// TREENI
drop table if exists treeni;
create table treeni(
	id int not null auto_increment,
	nimi varchar(100) not null,
	paiva int not null,
	alkaa time not null,
	paattyy time not null,
	tyyppi int not null,
	power varchar(1) default 'E' not null,
	voimassaalkaa date,
	voimassapaattyy date,
	constraint pk_treeni primary key(id),
	constraint uniikki_treeni unique (nimi, paiva),
	constraint treeni_tyyppi_viittaus foreign key(tyyppi) references treenityyppi(id),
);
insert into treeni(id, nimi, paiva, alkaa, paattyy, tyyppi) values (1, 'reeni', 2, parsedatetime('09:00', 'HH:mm'), parsedatetime('10:00', 'HH:mm'), 1);
insert into treeni(id, nimi, paiva, alkaa, paattyy, tyyppi) values (2, 'toinen reeni', 2, parsedatetime('11:00', 'HH:mm'), parsedatetime('12:00', 'HH:mm'), 1);

// TREENIVETAJA
drop table if exists treenivetaja;
create table treenivetaja(
	id int not null auto_increment,
	harrastaja int not null,
	treeni int not null,
	constraint pk_treenivetaja primary key(id),
	constraint uniikki_treenivetaja unique (harrastaja, treeni),
	constraint treenivetaja_harrastaja_viittaus foreign key(harrastaja) references harrastaja(id),
	constraint treenivetaja_treeni_viittaus foreign key(treeni) references treeni(id),
);
insert into treenivetaja(id, harrastaja, treeni) values (1, 1, 1);

// TREENISESSIO
drop table if exists treenisessio;
create table treenisessio(
	id int not null auto_increment,
	treeni int not null,
	paiva date not null,
	constraint pk_treenisessio primary key(id),
	constraint treenisessio_treeni_viittaus foreign key(treeni) references treeni(id),
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
	constraint treenisessiovetaja_treenisessio_viittaus foreign key(treenisessio) references treenisessio(id),
);
insert into treenisessiovetaja(id, harrastaja, treenisessio) values (1, 1, 1);



=== MAVEN REPOS ===

http://repository.jboss.org/nexus/content/groups/public-jboss/
http://anonsvn.icefaces.org/repo/maven2/releases/

=== TOOLS ====
WildFly 8.1.0.Final:	http://download.jboss.org/wildfly/8.1.0.Final/wildfly-8.1.0.Final.zip
Eclipse:				http://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/luna/R/eclipse-jee-luna-R-win32-x86_64.zip
Maven:					http://maven.apache.org/download.cgi?Preferred=ftp://mirror.reverse.net/pub/apache/
