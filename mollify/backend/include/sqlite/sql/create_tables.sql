BEGIN;

CREATE TABLE user (
  id INTEGER PRIMARY KEY,
  name varchar(255) NOT NULL,
  description varchar(255) NOT NULL DEFAULT '',
  password varchar(128) NULL,
  a1password varchar(128) NULL,
  permission_mode char(2) NULL,
  email varchar(128) NULL,
  auth varchar(8) NULL,
  is_group TINYINT(1) NOT NULL
);

CREATE TABLE user_group (
  user_id int(11) NOT NULL,
  group_id int(11) NOT NULL,
  PRIMARY KEY (user_id, group_id),
  KEY fk_ug_user (user_id),
  KEY fk_ug_group (group_id)
);

CREATE TABLE folder (
  id INTEGER PRIMARY KEY,
  name varchar(255) NOT NULL,
  path varchar(255) NOT NULL
);

CREATE TABLE item_description (
  item_id char(255) NOT NULL,
  description varchar(512) NOT NULL,
  PRIMARY KEY (item_id)
);

CREATE TABLE item_permission (
  user_id int(11) NULL DEFAULT 0,
  item_id char(255) NOT NULL,
  permission char(2) NOT NULL,
  PRIMARY KEY (user_id,item_id)
);

CREATE TABLE user_folder (
  user_id int(11) NOT NULL,
  folder_id int(11) NOT NULL,
  name varchar(255) NULL,
  PRIMARY KEY (user_id,folder_id),
  KEY fk_uf_folder (folder_id)
);

CREATE TABLE parameter (
  name char(255) NOT NULL,
  value char(255) NOT NULL,
  PRIMARY KEY (name)
);

CREATE TABLE event_log (
  id INTEGER PRIMARY KEY,
  time bigint(11) NOT NULL,
  user varchar(128) NULL,
  type varchar(128) NOT NULL,
  item varchar(512) NULL,
  details varchar(1024) NULL,
);

INSERT INTO parameter (name, value) VALUES ('version', '1_7_10');

COMMIT;
