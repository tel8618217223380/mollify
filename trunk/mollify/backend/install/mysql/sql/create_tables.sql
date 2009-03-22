CREATE TABLE `folder` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(255) NOT NULL,
  `path` varchar(255) NOT NULL,
  PRIMARY KEY  (`id`)
);

CREATE TABLE `user` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(255) NOT NULL,
  `password` varchar(128) NOT NULL,
  `permission_mode` char(2) character set latin1 NOT NULL,
  PRIMARY KEY  (`id`)
);

CREATE TABLE `user_folder` (
  `user_id` int(11) NOT NULL,
  `folder_id` int(11) NOT NULL,
  PRIMARY KEY  (`user_id`,`folder_id`),
  KEY `fk_pf_folder` (`folder_id`)
);

CREATE TABLE `parameter` (
  `name` char(255) character set latin1 NOT NULL,
  `value` char(255) character set latin1 NOT NULL,
  PRIMARY KEY  (`name`)
);
