CREATE TABLE `{TABLE_PREFIX}pending_registrations` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(255) NOT NULL,
  `password` varchar(128) NOT NULL,
  `email` varchar(128) NOT NULL,
  `key` varchar(128) NOT NULL,
  PRIMARY KEY (`id`)
) COLLATE utf8_general_ci COMMENT = 'Pending user registrations';

INSERT INTO `{TABLE_PREFIX}parameter` (name, value) VALUES ('plugin_registration_version', '1_0');