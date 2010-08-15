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
		
		public function __toString() {
			return "NotificatorDao";
		}
	}
?>