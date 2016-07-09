use `alice`;

create table if not exists `alice`.`stories` (
	`id` int unsigned auto_increment not null,
	`title` varchar(100) not null,
	`text` varchar(1000) not null,
	`deleted` boolean default false,
	primary key (`id`)
);
