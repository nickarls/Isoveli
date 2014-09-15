=== LUONTISCRIPTIT ===

drop table treenikaynti;
drop table treeni;
drop table harrastaja;

drop sequence hibernate_sequence;
create sequence hibernate_sequence;

create table harrastaja(
	id int not null,
	nimi varchar(100) not null,
	korttinumero varchar(100),
	constraint pk_harrastaja primary key(id),
	constraint uniikki_nimi unique(nimi),
	constraint uniikki_kortti unique(korttinumero)
);

insert into harrastaja (id, nimi, korttinumero) values (1, 'Nicklas Karlsson', '123');
insert into harrastaja (id, nimi, korttinumero) values (2, 'Erkki Seppä', '234');

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
	constraint pk_treenikaynti primary key(id),
	constraint harrastaja_viittaus foreign key(harrastaja) references harrastaja(id),
	constraint treeni_viittaus foreign key(treeni) references treeni(id),
)

=== MAVEN REPOS ===

http://repository.jboss.org/nexus/content/groups/public-jboss/
http://anonsvn.icefaces.org/repo/maven2/releases/

=== TOOLS ====
WildFly 8.1.0.Final:	http://download.jboss.org/wildfly/8.1.0.Final/wildfly-8.1.0.Final.zip
Eclipse:				http://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/luna/R/eclipse-jee-luna-R-win32-x86_64.zip
Maven:					http://maven.apache.org/download.cgi?Preferred=ftp://mirror.reverse.net/pub/apache/
