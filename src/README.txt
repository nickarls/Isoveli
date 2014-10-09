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
insert into osoite(id, osoite, postinumero, kaupunki) values (2, 'Trappaksentie 25', '06650', 'Hamari');

// YHTEYSTIETO
drop table if exists yhteystieto;
create table yhteystieto(
	id int not null auto_increment,
	puhelinnumero varchar(20),
	sahkoposti varchar(30),
	sahkopostilistalla varchar(1)
);
insert into yhteystieto(id, puhelinnumero, sahkoposti, sahkopostilistalla) values (1, '0405062266', 'nickarls@gmail.com', 'K');
insert into yhteystieto(id, puhelinnumero, sahkoposti, sahkopostilistalla) values (2, '0407218809', 'heidi.karlsson@abo.fi', 'K');

// HENKILÖ
drop table if exists henkilo;
create table henkilo(
	id int not null auto_increment,
	etunimi varchar(50) not null,
	sukunimi varchar(50) not null,
	osoite int,
	yhteystiedot int,
	salasana varchar(50),
	kuva blob,
	arkistoitu varchar(1) default 'E' not null,
	constraint pk_henkilo primary key(id),
	constraint osoite_viittaus foreign key(osoite) references osoite(id),	
	constraint yhteystieto_viittaus foreign key(yhteystiedot) references yhteystieto(id),	
);
insert into henkilo(id, etunimi, sukunimi, osoite, yhteystiedot, salasana) values (1, 'Nicklas', 'Karlsson', 1, 1, 'secret');
insert into henkilo(id, etunimi, sukunimi, osoite, yhteystiedot, salasana) values (2, 'Heidi', 'Karlsson', 2, 2, 'secret');

// HARRASTAJA
drop table if exists harrastaja;
create table harrastaja(
	id int not null auto_increment,
	henkilo int not null,
	huoltaja int,
	jasennumero varchar(10),
	korttinumero varchar(100),
	lisenssinumero varchar(10),
	syntynyt date not null,
	sukupuoli varchar(1) not null,
	constraint pk_harrastaja primary key(id),
	constraint uniikki_kortti unique(korttinumero),
	constraint uniikki_jasennumero unique(jasennumero),
	constraint uniikki_lisenssinumero unique(lisenssinumero),
	constraint harrastja_henkilo_viittaus foreign key(henkilo) references henkilo(id),
	constraint harrastaja_huoltaja_viittaus foreign key(huoltaja) references henkilo(id),
);
insert into harrastaja (id, henkilo, jasennumero, korttinumero, lisenssinumero, syntynyt, sukupuoli) values (1, 1, '666', '123', '666', parsedatetime('28.06.1975', 'dd.MM.yyyy'), 'M');
insert into harrastaja (id, henkilo, jasennumero, korttinumero, lisenssinumero, syntynyt, sukupuoli) values (2, 2, '667', '124', '667', parsedatetime('09.08.1976', 'dd.MM.yyyy'), 'N');

// ROOLI
drop table if exists rooli;
create table rooli(
	id int not null auto_increment,
	nimi varchar(20) not null,
	constraint pk_rooli primary key(id),
	constraint uniikki_roolinimi unique(nimi)
);
insert into rooli(id, nimi) values (1, 'Ylläpitäjä');
insert into rooli(id, nimi) values (2, 'Treenien vetäjä');


// HENKILOROOLI
drop table if exists henkilorooli;
create table henkilorooli(
	henkilo int not null,
	rooli int not null,
	constraint uniikki_kayttajarooli unique(henkilo, rooli),
	constraint kayttajarooli_harrastaja_viittaus foreign key(henkilo) references henkilo(id),
	constraint kayttajarooli_rooli_viittaus foreign key(rooli) references rooli(id)
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
	constraint kisatulos_harrastaja_viittaus foreign key(harrastaja) references harrastaja(id),
);
insert into kisatulos(id, harrastaja, tyyppi, paiva, kisa, sarja, tulos) values (1, 1, 1, parsedatetime('12.12.2006', 'dd.MM.yyyy'), 'Masters Cup', 'Seniorit', '1');

// SOPIMUSTYYPPI
drop table if exists sopimustyyppi;
create table sopimustyyppi(
	id int not null auto_increment,
	nimi varchar(50) not null,
	jasenmaksu varchar(1) not null default 'E',
	jatkuva varchar(1) not null default 'E',
	treenikertoja varchar(1) not null default 'E',
	alkeiskurssi varchar(1) not null default 'E',
	koeaika varchar(1) not null default 'E',
	vapautus varchar(1) not null default 'E',
	power varchar(1) not null default 'E',
	perhealennus varchar(1) not null default 'E',
	oletuskuukaudetvoimassa int default 0,
	oletusmaksuvali int default 0,
	oletustreenikerrat int default 0,
	constraint pk_sopimustyyppi primary key(id),
	constraint uniikki_sopimusnimi unique(nimi)
);
insert into sopimustyyppi(id, nimi, jasenmaksu) values (1, 'Jäsenmaksu', 'K');
insert into sopimustyyppi(id, nimi, jatkuva, oletusmaksuvali) values (2, 'Jatkuva', 'K', 12);
insert into sopimustyyppi(id, nimi, treenikertoja, oletustreenikerrat) values (3, 'Kymppikerta', 'K', 10);
insert into sopimustyyppi(id, nimi, alkeiskurssi, oletuskuukaudetvoimassa) values (4, 'Alkeiskurssi', 'K', 3);
insert into sopimustyyppi(id, nimi, koeaika) values (5, 'Koeaika', 'K');
insert into sopimustyyppi(id, nimi, vapautus) values (6, 'Vapautus', 'K');
insert into sopimustyyppi(id, nimi, power) values (7, 'Power', 'K');
insert into sopimustyyppi(id, nimi, perhealennus) values (8, 'Perhealennus', 'K');

// SOPIMUS
drop table if exists sopimus;
create table sopimus(
	id int not null auto_increment,
	harrastaja int not null,
	tyyppi int not null,
	umpeutuu date,
	treenikertoja int default 0,
	maksuvali int default 6,
	constraint pk_sopimus primary key(id),
	constraint sopimus_harrastaja_viittaus foreign key(harrastaja) references harrastaja(id),
	constraint sopimus_tyyppi_viittaus foreign key(tyyppi) references sopimustyyppi(id),
);
insert into sopimus(id, harrastaja, tyyppi, umpeutuu, maksuvali) values (1, 1, 1, parsedatetime('31.12.2014', 'dd.MM.yyyy'), 6); 
insert into sopimus(id, harrastaja, tyyppi) values (2, 1, 6); 

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
