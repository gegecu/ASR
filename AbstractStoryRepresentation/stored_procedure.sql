-- --------------------------------------------------------------------------------
-- Routine DDL
-- Note: comments before and after the routine body will not be stored by the server
-- --------------------------------------------------------------------------------
DELIMITER $$

CREATE DEFINER=`root`@`127.0.0.1` PROCEDURE `four_hops`(in conceptFrom varchar(100))
BEGIN
	declare child_id, prev_id, hops int;
	set hops = 0;
	
	create temporary table if not exists `concepts_mapping` as (select `conceptsFrom`.`concept` as `conceptFrom`, `conceptsFrom`.`id` as `conceptFromId`, `relations`.`relation` as `relation`, `relations`.`id` as `relationId`, `conceptsTo`.`concept` as `conceptTo`, `conceptsTo`.`id` as `conceptToId`
				from (select `id`, `concept`, `posID` from `concepts`) as `conceptsFrom`,`relations` as `relations`, `concept_relations` as `concept_relations` 
				left join `concepts` as `conceptsTo` on `concept_relations`.`toID` = `conceptsTo`.`id` 
				where `concept_relations`.`fromID` = `conceptsFrom`.`id` and `concept_relations`.`relationID` = `relations`.`id`);

	select `concepts_mapping`.`conceptFromId` into prev_id from `concepts_mapping` where `concepts_mapping`.`conceptFrom` = conceptFrom limit 1; 
	select `concepts_mapping`.`conceptToId` into child_id from `concepts_mapping` where `concepts_mapping`.`conceptFrom` = conceptFrom and `concepts_mapping`.`relationId` = 7 order by rand() limit 1;

	create temporary table if not exists temp_table (select * from `concepts_mapping` limit 1);
	truncate table temp_table;

	while hops < 4 and child_id <> 0 do
		insert into temp_table select * from `concepts_mapping` where `concepts_mapping`.`conceptFromId` = prev_id and `concepts_mapping`.`conceptToId` = child_id limit 1;
		set prev_id = child_id;
		select `concepts_mapping`.`conceptToId` into child_id from `concepts_mapping` where `concepts_mapping`.`conceptFromId` = prev_id and `concepts_mapping`.`relationId` = 7  order by rand() limit 1;
		set hops = hops + 1;
	end while;
	select * from temp_table;
END