<?php

	/**
	 * Copyright (c) 2008- Samuli JŠrvelŠ
	 *
	 * All rights reserved. This program and the accompanying materials
	 * are made available under the terms of the Eclipse Public License v1.0
	 * which accompanies this distribution, and is available at
	 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
	 * this entire header must remain intact.
	 */
	
	class NotificatorDao {
		private $env;

		public function __construct($env) {
			$this->env = $env;
		}
		
		public function getAllNotifications() {
			$db = $this->env->configuration()->db();
			$result = $db->query("select `id`, `name` from ".$db->table("notificator_notification")." order by id asc")->rows();
			return $result;
		}

		public function getNotification($id) {
			$db = $this->env->configuration()->db();
			
			$query = "select ntf.`id`, ntf.`name`, ntf.`message_title`, ntf.`message`, evt.`event_type`, ntf_user.`id` as ntf_usr_id, ntf_user.`name` as ntf_usr_name, ntf_user.`email` as ntf_usr_email, ntf_rcp_user.`id` as ntf_rcp_usr_id, ntf_rcp_user.`name` as ntf_rcp_usr_name, ntf_rcp_user.`email` as ntf_rcp_usr_email ";
			
			$query .= "from ".$db->table("notificator_notification")." ntf left outer join ".$db->table("notificator_notification_event")." evt on evt.`notification_id` = ntf.`id` left outer join ".$db->table("notificator_notification_user")." ntf_usr on ntf_usr.`notification_id` = ntf.`id` left outer join ".$db->table("user")." ntf_user on ntf_user.`id` = ntf_usr.`user_id` left outer join ".$db->table("notificator_notification_recipient")." ntf_rcp on ntf_rcp.`notification_id` = ntf.`id` left outer join ".$db->table("user")." ntf_rcp_user on ntf_rcp_user.`id` = ntf_rcp.`user_id`";
			
			$query .= "where ntf.`id` = ".$db->string($id, TRUE);
			
			$rows = $db->query($query)->rows();
			if (count($rows) == 0) return FALSE;
			
			$first = $rows[0];
			$result = array(
				"id" => $first["id"],
				"name" => $first["name"],
				"message" => $first["message"],
				"message_title"  => $first["message_title"],
				"events" => array(),
				"users" => array(),
				"recipients" => array()
			);
			
			$events = NULL;
			$recipientIds = array();
			
			foreach($rows as $row) {
				$event = $row["event_type"];
				if ($event != NULL and !in_array($event, $result["events"]))
					$result["events"][] = $event;
					
				$recipient = $row["ntf_rcp_usr_id"];
				if ($recipient != NULL and !in_array($recipient, $recipientIds)) {
					$recipientIds[] = $recipient;
					$result["recipients"][] = array("id" => $recipient, "name" => $row["ntf_rcp_usr_name"], "email" => $row["ntf_rcp_usr_email"]);
				}
			}
			
			return $result;
		}

		public function findNotifications($typeId, $userId) {
			$db = $this->env->configuration()->db();
			
			$query = "select distinct ntf.`id`, ntf.`name`, ntf.`message_title`, ntf.`message`, rcp.`user_id`, usr.`email` from ".$db->table("notificator_notification")." ntf, ".$db->table("notificator_notification_event")." evt, ".$db->table("notificator_notification_user")." ntf_usr, ".$db->table("notificator_notification_recipient")." rcp, ".$db->table("user")." usr";
			$query .= " where evt.`notification_id` = ntf.`id` and ntf_usr.`notification_id` = ntf.`id` and rcp.`notification_id` = ntf.`id` and usr.`id` = ntf_usr.`user_id`";
			$query .= " and (ntf_usr.`user_id` = 0 or ntf_usr.`user_id` ".($userId == NULL ? "is null" : " = '$userId'").")";
			$query .= " and (evt.`event_type` is null or evt.`event_type` = '$typeId')";
			$query .= " order by ntf.`id` asc";
			$rows = $db->query($query)->rows();
			
			$result = array();
			$recipients = array();
			$prev = NULL;
			
			foreach($result as $row) {
				if ($prev == NULL) $prev = $row;

				if ($prev["id"] != $row["id"]) {
					$result[] = new Notification($prev["id"], $prev["name"], $prev["message_title"], $prev["message"], $recipients);
					$recipients = array();
				}
				$recipients[] = array("id" => $row["user_id"], "email" => $row["email"]);				
			}
			
			return $result;
		}
		
		public function addNotification($data) {
			$db = $this->env->configuration()->db();
			$db->update(sprintf("INSERT INTO ".$db->table("notificator_notification")." (name) VALUES ('%s')", $db->string($data["name"])));
			return $db->lastId();
			return TRUE;
		}

		public function editNotificationName($id, $name) {
			$db = $this->env->configuration()->db();
			$affected = $db->update(sprintf("UPDATE ".$db->table("notificator_notification")." SET name = '%s' where id=%s", $db->string($name), $db->string($id)));
			if ($affected != 1) throw new ServiceException("REQUEST_FAILED", "Invalid update for id=".$id);
			return TRUE;
		}

		public function editNotificationMessage($id, $title, $message) {
			$db = $this->env->configuration()->db();
			$affected = $db->update(sprintf("UPDATE ".$db->table("notificator_notification")." SET message_title = '%s', message = '%s' where id=%s", $db->string($title), $db->string($message), $db->string($id)));
			if ($affected != 1) throw new ServiceException("REQUEST_FAILED", "Invalid update for id=".$id);
			return TRUE;
		}

		public function editNotificationEvents($id, $events) {
			$db = $this->env->configuration()->db();
			
			$db->startTransaction();
			$db->update(sprintf("DELETE FROM ".$db->table("notificator_notification_event")." WHERE notification_id = '%s'", $db->string($id)));
			foreach ($events as $event)
				$db->update(sprintf("INSERT INTO ".$db->table("notificator_notification_event")." (notification_id, event_type) VALUES ('%s', '%s')", $db->string($id), $db->string($event)));
			$db->commit();
			
			return TRUE;
		}
					
		public function __toString() {
			return "NotificatorDao";
		}
	}
?>