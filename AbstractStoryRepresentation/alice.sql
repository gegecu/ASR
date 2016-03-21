create database if not exists `alice`;
use `alice`;

create table if not exists `alice`.`part_of_speeches` (
	`id` int unsigned auto_increment not null,
	`partOfSpeech` varchar(20) not null,
	primary key (`id`),
	unique (`partOfSpeech`)
);

create table if not exists `alice`.`concepts` (
	`id` int unsigned auto_increment not null,
	`concept` varchar(50) not null,
	`posID` int unsigned not null,
	foreign key (`posID`) references `part_of_speeches`(`id`),
	primary key (`id`),
	unique (`concept`, `posID`)
);

create table if not exists `alice`.`words` (
	`id` int unsigned auto_increment not null,
	`word` varchar(50) not null,
	`posID` int unsigned not null,
	foreign key (`posID`) references `part_of_speeches`(`id`),
	primary key (`id`),
	unique (`word`, `posID`)
);

create table if not exists `alice`.`concept_to_words` (
	`id` int unsigned auto_increment not null,
	`conceptID` int unsigned not null,
	`wordID` int unsigned not null,
	foreign key (`conceptID`) references `concepts`(`id`) on delete cascade,
	foreign key (`wordID`) references `words`(`id`) on delete cascade,
	primary key (`id`)
);

create table if not exists `alice`.`relations` (
	`id` int unsigned auto_increment not null,
	`relation` varchar(50) not null,
	primary key (`id`),
	unique (`relation`)
);

create table if not exists `alice`.`concept_relations` (
	`id` int unsigned auto_increment not null,
	`fromID` int unsigned not null,
	`relationID` int unsigned not null,
	`toID` int unsigned not null,
	foreign key (`fromID`) references `concepts`(`id`) on delete cascade,
	foreign key (`relationID`) references `relations`(`id`) on delete cascade,
	foreign key (`toID`) references `concepts`(`id`) on delete cascade,
	primary key (`id`),
    unique (`fromID`, `relationID`, `toID`)
);

insert into `alice`.`part_of_speeches` (`partOfSpeech`) values ('noun');
insert into `alice`.`part_of_speeches` (`partOfSpeech`) values ('verb');
insert into `alice`.`part_of_speeches` (`partOfSpeech`) values ('adjective');
insert into `alice`.`relations` (`relation`) values ('capableOf');
insert into `alice`.`relations` (`relation`) values ('hasProperty');
insert into `alice`.`relations` (`relation`) values ('hasA');
insert into `alice`.`relations` (`relation`) values ('isA');
insert into `alice`.`concepts` (`concept`, `posID`) values ('boy', 1);
insert into `alice`.`concepts` (`concept`, `posID`) values ('park', 1);
insert into `alice`.`concepts` (`concept`, `posID`) values ('ball', 1);
insert into `alice`.`concepts` (`concept`, `posID`) values ('person', 1);
insert into `alice`.`concepts` (`concept`, `posID`) values ('location', 1);
insert into `alice`.`concepts` (`concept`, `posID`) values ('object', 1);
insert into `alice`.`words` (`word`, `posID`) values ('boy', 1);
insert into `alice`.`words` (`word`, `posID`) values ('park', 1);
insert into `alice`.`words` (`word`, `posID`) values ('ball', 1);
insert into `alice`.`words` (`word`, `posID`) values ('person', 1);
insert into `alice`.`words` (`word`, `posID`) values ('object', 1);
insert into `alice`.`words` (`word`, `posID`) values ('location', 1);
insert into `alice`.`concept_relations` (`fromID`, `relationID`, `toID`) values (3, 4, 6);
insert into `alice`.`concept_relations` (`fromID`, `relationID`, `toID`) values (1, 4, 4);
insert into `alice`.`concept_relations` (`fromID`, `relationID`, `toID`) values (2, 4, 5);


