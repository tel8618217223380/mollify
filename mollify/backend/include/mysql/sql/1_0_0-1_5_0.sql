UPDATE `{TABLE_PREFIX}parameter` SET value = '1_5_0' WHERE name = 'version';

ALTER TABLE `{TABLE_PREFIX}user` ADD `is_group` TINYINT(1) NOT NULL;

UPDATE `{TABLE_PREFIX}user` SET `is_group` = '0';

CREATE TABLE `{TABLE_PREFIX}user_group` (
  `user_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`, `group_id`),
  KEY `fk_ug_user` (`user_id`),
  KEY `fk_ug_group` (`group_id`)
) COLLATE utf8_general_ci COMMENT = 'Mollify user groups';