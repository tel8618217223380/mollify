CREATE TABLE `user` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(255) NOT NULL,
  `password` varchar(128) NOT NULL,
  `permission_mode` char(2) NOT NULL,
  PRIMARY KEY (`id`)
) COLLATE utf8_general_ci COMMENT = 'Mollify users';

CREATE TABLE `folder` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(255) NOT NULL,
  `path` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) COLLATE utf8_general_ci COMMENT = 'Mollify published folders';

CREATE TABLE `item_description` (
  `item_id` char(255) NOT NULL,
  `description` varchar(512) NOT NULL,
  PRIMARY KEY (`item_id`)
) COLLATE utf8_general_ci COMMENT = 'Mollify item descriptions';

CREATE TABLE `item_permission` (
  `user_id` char(11) NULL DEFAULT '0',
  `item_id` char(255) NOT NULL,
  `permission` char(2) NOT NULL,
  PRIMARY KEY (`user_id`,`item_id`)
) COLLATE utf8_general_ci COMMENT = 'Mollify item permissions';

CREATE TABLE `user_folder` (
  `user_id` int(11) NOT NULL,
  `folder_id` int(11) NOT NULL,
  `name` varchar(255) NULL,
  PRIMARY KEY (`user_id`,`folder_id`),
  KEY `fk_uf_folder` (`folder_id`)
) COLLATE utf8_general_ci COMMENT = 'Mollify user published folders';

CREATE TABLE `parameter` (
  `name` char(255) NOT NULL,
  `value` char(255) NOT NULL,
  PRIMARY KEY (`name`)
) COLLATE utf8_general_ci COMMENT = 'Mollify parameters';
