UPDATE parameter SET value = '2_4' WHERE name = 'version';

CREATE TABLE permission (
  name char(64) NOT NULL,
  user_id int(11) NULL DEFAULT 0,
  subject char(255) NULL,
  value char(32) NOT NULL,
  PRIMARY KEY (name,user_id,subject)
);

INSERT INTO permission (name, user_id, subject, value) SELECT 'filesystem_item_access' as name, user_id, item_id as subject, permission as value FROM item_permission;