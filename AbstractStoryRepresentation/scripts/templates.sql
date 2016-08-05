CREATE TABLE IF NOT EXISTS `alice`.`template_group` (
	`id` int unsigned auto_increment not null,
	`name` varchar(30) not null,
	primary key(`id`)
);

CREATE TABLE IF NOT EXISTS `alice`.`templates` (
	`id` int unsigned auto_increment not null,
	`group_id` int unsigned not null,
	`template` varchar(100) not null,
	foreign key (`group_id`) references `template_group`(`id`),
	primary key(`id`)
);