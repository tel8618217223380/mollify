CREATE TABLE `{TABLE_PREFIX}notificator_notification` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) COLLATE utf8_general_ci COMMENT = 'Notifications';

CREATE TABLE `{TABLE_PREFIX}notificator_notification_event` (
  `notification_id` int(11) NOT NULL,
  `event_type` varchar(128) NOT NULL,
  PRIMARY KEY (`notification_id`, `event_type`),
  KEY `fk_notificator_notification_event_1` (`notification_id`)
) COLLATE utf8_general_ci COMMENT = 'Notification event criteria';

CREATE TABLE `{TABLE_PREFIX}notificator_notification_user` (
  `notification_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`notification_id`, `user_id`),
  KEY `fk_notificator_notification_user_1` (`notification_id`),
  KEY `fk_notificator_notification_user_2` (`user_id`)
) COLLATE utf8_general_ci COMMENT = 'Notification user criteria';

CREATE TABLE `{TABLE_PREFIX}notificator_notification_item` (
  `notification_id` int(11) NOT NULL,
  `item_id` varchar(128) NOT NULL,
  PRIMARY KEY (`notification_id`, `item_id`),
  KEY `fk_notificator_notification_item_1` (`notification_id`)
) COLLATE utf8_general_ci COMMENT = 'Notification item criteria';

CREATE TABLE `{TABLE_PREFIX}notificator_notification_recipient` (
  `notification_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`notification_id`, `user_id`),
  KEY `fk_notificator_notification_recipient_1` (`notification_id`),
  KEY `fk_notificator_notification_recipient_2` (`user_id`)
) COLLATE utf8_general_ci COMMENT = 'Notification recipients';

INSERT INTO `{TABLE_PREFIX}parameter` (name, value) VALUES ('plugin_Notificator_version', '1_0');