CREATE TABLE IF NOT EXISTS `alice`.`template_group` (
	`id` int unsigned auto_increment not null,
	`name` varchar(100) not null,
	primary key(`id`)
);

insert into `alice`.`template_group` (`id`, `name`) values 
(1, 'atLocationStorySegmentStart'),
(2, 'atLocationStorySegmentDirectObject'),
(3, 'hasAStorySegment'),
(4, 'isAStorySegment'),
(5, 'hasPropertyStorySegment'),
(6, 'causesNoun'),
(7, 'causesVerb'),
(8, 'causesAdjective'),
(9, 'nounStartDirective'),
(10, 'causeEffectDirectivePhraseFormat');

CREATE TABLE IF NOT EXISTS `alice`.`templates` (
	`id` int unsigned auto_increment not null,
	`group_id` int unsigned not null,
	`template` varchar(300) not null,
	foreign key (`group_id`) references `template_group`(`id`),
	primary key(`id`),
    unique (`group_id`, `template`)
);

insert into `alice`.`templates` (`group_id`, `template`) values (1, 'There is <start> in <end>.'), (1, '<end> has <start>.');
insert into `alice`.`templates` (`group_id`, `template`) values (2, '<doer> is in <end>.');
insert into `alice`.`templates` (`group_id`, `template`) values (3, '<start> has <end>.');
insert into `alice`.`templates` (`group_id`, `template`) values (4, '<start> is <end>.');
insert into `alice`.`templates` (`group_id`, `template`) values (5, '<start> can be <end>.');
insert into `alice`.`templates` (`group_id`, `template`) values (6, '<start> produces <end>.');
insert into `alice`.`templates` (`group_id`, `template`) values (7, '<doer> <end> <object>.');
insert into `alice`.`templates` (`group_id`, `template`) values (8, '<doer> became <end>.');
insert into `alice`.`templates` (`group_id`, `template`) values (9, 'Describe <noun>.'), (9, 'Tell me more about <noun>.'), (9, 'Write more about <noun>.'), (9, 'I want to hear more about <noun>.'), (9, 'Tell something more about <noun>.');
insert into `alice`.`templates` (`group_id`, `template`) values (10, 'Tell me why <phrase>.'), (10, 'Explain why <phrase>.'), (10, 'Write more about why <phrase>.'), (10, 'Write the reason why <phrase>.');

CREATE TABLE IF NOT EXISTS `alice`.`specific_topic_group` (
	`id` int unsigned auto_increment not null,
	`name` varchar(100) not null,
	primary key(`id`)
);

insert into `alice`.`specific_topic_group` (`id`, `name`) values (1, 'person'), (2, 'object');

CREATE TABLE IF NOT EXISTS `alice`.`specific_topics` (
	`id` int unsigned auto_increment not null,
	`group_id` int unsigned not null,
	`topic` varchar(100) not null,
	foreign key (`group_id`) references `specific_topic_group`(`id`),
	primary key(`id`),
    unique (`group_id`, `topic`)
);

insert into `alice`.`specific_topics` (`group_id`, `topic`) values (1, 'attitude'), (1, 'nationality'), (1, 'talent');
insert into `alice`.`specific_topics` (`group_id`, `topic`) values (2, 'color'), (2, 'shape'), (2, 'size'), (2, 'texture');

CREATE TABLE IF NOT EXISTS `alice`.`data_group` (
	`id` int unsigned auto_increment not null,
	`name` varchar(100) not null,
	primary key(`id`)
);

insert into `alice`.`data_group` (`id`, `name`) values (1, 'futureTenseAuxs'), (2, 'presentTenseAuxs'), (3, 'pastTenseAuxs');
insert into `alice`.`data_group` (`id`, `name`) values (4, 'negatives'), (5, 'positives');
insert into `alice`.`data_group` (`id`, `name`) values (6, 'locationVerbs');

CREATE TABLE IF NOT EXISTS `alice`.`data` (
	`id` int unsigned auto_increment not null,
	`group_id` int unsigned not null,
	`data` varchar(100) not null,
	foreign key (`group_id`) references `data_group`(`id`),
	primary key(`id`),
    unique (`group_id`, `data`)
);

insert into `alice`.`data` (`group_id`, `data`) values (1, 'will'), (1, 'must'), (1, 'shall'), (1, 'should'), (1, 'would'), (1, 'can'), (1, 'could'), (1, 'need'), (1, 'ought'), (1, 'may'), (1, 'might');
insert into `alice`.`data` (`group_id`, `data`) values (2, 'am'), (2, 'are'), (2, 'is'), (2, 'do'), (2, 'does');
insert into `alice`.`data` (`group_id`, `data`) values (3, 'was'), (3, 'were'), (3, 'did'), (3, 'had'), (3, 'have'), (3, 'has'), (3, 'having'), (3, 'been');
insert into `alice`.`data` (`group_id`, `data`) values (4, 'hate'), (4, 'hates'), (4, 'dislike'), (4, 'dislikes');
insert into `alice`.`data` (`group_id`, `data`) values (5, 'like'), (5, 'likes'), (5, 'love'), (5, 'loves');
insert into `alice`.`data` (`group_id`, `data`) values (6, 'go'), (6, 'climb'), (6, 'run'), (6, 'walk'), (6, 'swim'), (6, 'travel');