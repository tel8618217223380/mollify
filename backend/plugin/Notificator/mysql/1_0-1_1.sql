UPDATE `{TABLE_PREFIX}parameter` SET value = '1_1' WHERE name = 'plugin_Notificator_version';

ALTER TABLE `{TABLE_PREFIX}notificator_notification_event` ADD `id` int(11) NOT NULL auto_increment AFTER `notification_id`;

CREATE TABLE `{TABLE_PREFIX}notificator_notification_event_filter` (
  `notification_event_id` int(11) NOT NULL,
  `type` varchar(128) NOT NULL,
  `value` varchar(128) NOT NULL,
  PRIMARY KEY (`notification_event_id`, `type`),
  KEY `fk_notificator_notification_event_type_1` (`notification_event_id`)
) ENGINE = '{ENGINE}' COLLATE utf8_general_ci COMMENT = 'Notification event filter';