=== LUONTISCRIPTIT ===

drop table if exists treenikaynti;
drop table if exists treeni;
drop table if exists harrastaja;
drop table if exists osoite;
drop table if exists henkilo;
drop table if exists vyoarvo;
drop table if exists vyokoe;

drop sequence if exists harrastaja_seq;
create sequence harrastaja_seq start with 2;
drop sequence if exists henkilo_seq;
create sequence henkilo_seq start with 2;
drop sequence if exists osoite_seq;
create sequence osoite_seq start with 2;
drop sequence if exists vyokoe_seq;
create sequence vyokoe_seq start with 2;

create table osoite(
	id int not null,
	osoite varchar(200) not null,
	postinumero varchar(10) not null,
	kaupunki varchar(50) not null,
	constraint pk_osoite primary key(id)
);

insert into osoite(id, osoite, postinumero, kaupunki) values (1, 'Vaakunatie 10 as 7', '20780', 'Kaarina');

create table henkilo(
	id int not null,
	etunimi varchar(50) not null,
	sukunimi varchar(50) not null,
	osoite int,
	sahkoposti varchar(50),
	sahkopostilistalla varchar(1),
	constraint pk_henkilo primary key(id),
	constraint uniikki_henkilo_nimi unique(etunimi, sukunimi),
	constraint osoite_viittaus foreign key(osoite) references osoite(id),	
);

insert into henkilo(id, etunimi, sukunimi, osoite, sahkoposti, sahkopostilistalla) values (1, 'Nicklas', 'Karlsson', 1, 'nickarls@gmail.com', 'K');

create table harrastaja(
	id int not null,
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

insert into harrastaja (id, henkilo, jasennumero, korttinumero, lisenssinumero, syntynyt, sukupuoli) values (1, 1, 666, '123', '666', parsedatetime('28.06.1975', 'dd.MM.yyyy'), 'M');

create table treeni(
	id int not null,
	kuvaus varchar(100) not null,
	paiva int not null,
	alkaa time not null,
	paattyy time not null,
	constraint pk_treeni primary key(id),
	constraint uniikki_treeni unique (kuvaus, paiva)
);

insert into treeni(id, kuvaus, paiva, alkaa, paattyy) values (1, 'reeni', 2, parsedatetime('09:00', 'HH:mm'), parsedatetime('10:00', 'HH:mm'));
insert into treeni(id, kuvaus, paiva, alkaa, paattyy) values (2, 'toinen reeni', 2, parsedatetime('11:00', 'HH:mm'), parsedatetime('12:00', 'HH:mm'));

create table treenikaynti(
	id int not null,
	aikaleima timestamp not null,	
	harrastaja int not null,
	treeni int not null,
	paiva date not null,
	constraint pk_treenikaynti primary key(id),
	constraint treenikaynti_harrastaja_viittaus foreign key(harrastaja) references harrastaja(id),
	constraint treenikaynti_treeni_viittaus foreign key(treeni) references treeni(id),
	constraint uniikki_treenikaynti unique(harrastaja, treeni, paiva)
);

create table vyoarvo(
	id int not null,
	nimi varchar(10) not null,
	minimikuukaudet int,
	minimitreenit int,
	constraint pk_vyoarvo primary key(id),
	constraint uniikki_vyonimi unique(nimi)
);

insert into vyoarvo (id, nimi, minimikuukaudet, minimitreenit) values (1, '8.kup', 2, 15);
insert into vyoarvo (id, nimi, minimikuukaudet, minimitreenit) values (2, '7.kup', 3, 25);
insert into vyoarvo (id, nimi, minimikuukaudet, minimitreenit) values (3, '6.kup', 3, 25);
insert into vyoarvo (id, nimi, minimikuukaudet, minimitreenit) values (4, '5.kup', 3, 25);
insert into vyoarvo (id, nimi, minimikuukaudet, minimitreenit) values (5, '4.kup', 4, 35);
insert into vyoarvo (id, nimi, minimikuukaudet, minimitreenit) values (6, '3.kup', 4, 35);
insert into vyoarvo (id, nimi, minimikuukaudet, minimitreenit) values (7, '2.kup', 5, 40);
insert into vyoarvo (id, nimi, minimikuukaudet, minimitreenit) values (8, '1.kup', 6, 50);
insert into vyoarvo (id, nimi, minimikuukaudet, minimitreenit) values (9, '1.dan', 6, 60);
insert into vyoarvo (id, nimi, minimikuukaudet, minimitreenit) values (10, '2.dan', 24, 200);
insert into vyoarvo (id, nimi, minimikuukaudet, minimitreenit) values (11, '3.dan', 36, 300);

create table vyokoe(
	id int not null,
	harrastaja int not null,
	vyoarvo int not null,
	paiva date not null,
	constraint uniikki_koe unique(harrastaja, vyoarvo),
	constraint vyokoe_harrastaja_viittaus foreign key(harrastaja) references harrastaja(id),
	constraint vyokoe_vyoarvo_viittaus foreign key(vyoarvo) references vyoarvo(id)
);

insert into vyokoe(id, harrastaja, vyoarvo, paiva) values (1, 1, 8, parsedatetime('12.12.2006', 'dd.MM.yyyy'));

=== MAVEN REPOS ===

http://repository.jboss.org/nexus/content/groups/public-jboss/
http://anonsvn.icefaces.org/repo/maven2/releases/

=== TOOLS ====
WildFly 8.1.0.Final:	http://download.jboss.org/wildfly/8.1.0.Final/wildfly-8.1.0.Final.zip
Eclipse:				http://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/luna/R/eclipse-jee-luna-R-win32-x86_64.zip
Maven:					http://maven.apache.org/download.cgi?Preferred=ftp://mirror.reverse.net/pub/apache/
