UPDATE `{TABLE_PREFIX}parameter` SET value = '1_8_3' WHERE name = 'version';

ALTER TABLE `{TABLE_PREFIX}user_folder` ADD `path_prefix` varchar(255) NULL AFTER `name`;