<?php

	/**
	 * NotificatorHandler.class.php
	 *
	 * Copyright 2008- Samuli Järvelä
	 * Released under GPL License.
	 *
	 * License: http://www.mollify.org/license.php
	 */
	
	class NotificatorHandler {
		private $env;
		
		public function __construct($env) {
			$this->env = $env;
		}
		
		public function onEvent($e) {
			//$type = $e->typeId();
			$userId = $this->getUserId($e);

			$notifications = $this->findNotificationsForEvent($e, $userId);
			$this->sendNotifications($notifications, $e);
		}
		
		private function findNotificationsForEvent($event, $userId) {
			require_once("Notification.class.php");
			require_once("dao/NotificatorDao.class.php");
			
			$dao = new NotificatorDao($this->env);
			$notifications = $dao->findNotifications($event->typeId(), $userId);			
			return $this->filterNotifications($notifications, $event);
		}

		private function sendNotifications($notifications, $e) {
			Logging::logDebug("NOTIFICATOR: Found ".count($notifications)." notifications for event: ".$e);
			
			if (!$this->env->features()->isFeatureEnabled("mail_notification")) {
				Logging::logError("Mail notification not enabled, notifications not sent");
				return;
			}
			
			foreach($notifications as $notification)
				$this->sendNotification($notification, $e);
		}
		
		private function filterNotifications($list, $event) {
			$result = array();
			//TODO get filters for all notifications
			$allFilters = array();
			foreach($list as $notification) {
				if (!$this->isNotificationFiltered($notification, $allFilters))
					$result[] = $notification;
			}
			return $result;
		}
		
		private function isNotificationFiltered($notification, $e, $allFilters) {
			$filtered = FALSE;
			$eventType = $e->typeId();
			foreach($notification["events"] as $event) {
				if ($event["type"] != $eventType) continue;
				// if matching event does not have filters, notification passes
				if (!in_array($event["id"], $allFilters)) return FALSE;
				
				foreach($allFilters[$event["id"]] as $filter) {
					if (!$this->isFilterMatch($filter, $e)) {
						if (Logging::isDebug()) Logging::logDebug("Filter does not match: ".$filter["type"]." (".$filter["id"].")");
						$filtered = TRUE;
					}
				}
			}
			return $filtered;
		}
		
		private function isFilterMatch($filter, $e) {
			if ($filter["type"] == "item_name") {
				$name = $filter["value"];
				return preg_match($name, $e->item()->name());
			}
			if ($filter["type"] == "item_any_parent") {
				//$item = $filter["value"];
				$itemLocation = "";	//TODO get item by id
				$parentLocation = $e->item()->location();
				return (strcmp(substr($itemLocation, 0, strlen($parentLocation)), $parentLocation) == 0);
			}
			if ($filter["type"] == "item_parent") {
				$parentId = $filter["value"];
				return (strcmp($e->item()->parent()->id(), $parentId) == 0);
			}
			throw new ServiceException("INVALID_CONFIGURATION", "No filter supported: ".$filter["type"]);
		}

		private function sendNotification($notification, $e) {
			$values = $e->values($this->env->formatter());
			$title = $this->getTitle($notification, $values);
			$message = $this->getMessage($notification, $values);
			
			if (Logging::isDebug())
				Logging::logDebug("NOTIFICATOR: Sending notification ".$notification->id().":".$message);
			$this->env->mailer()->send($notification->getRecipients(), $title, $message);
		}

		private function getTitle($notification, $values) {			
			return Util::replaceParams($notification->getTitle(), $values);
		}
		
		private function getMessage($notification, $values) {
			return Util::replaceParams($notification->getMessage(), $values);
		}

		private function getUserId($e) {
			$user = $e->user();
			if (!$user) return NULL;
			return $user["user_id"];
		}
		
		public function __toString() {
			return "NotificatorHandler";
		}
	}
?>