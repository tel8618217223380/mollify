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
			$type = $e->typeId();
			$userId = $this->getUserId($e);

			$notifications = $this->findNotifications($type, $userId);
			$this->sendNotifications($notifications, $e);
		}
		
		private function findNotifications($type, $userId) {
			require_once("Notification.class.php");
			require_once("dao/NotificatorDao.class.php");
			
			$dao = new NotificatorDao($this->env);
			return $dao->findNotifications($type, $userId);
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

		private function sendNotification($notification, $e) {
			$values = $e->values($this->env->formatter());
			$title = $this->getTitle($notification, $values);
			$message = $this->getMessage($notification, $values);
			
			if (Logging::isDebug())
				Logging::logDebug("NOTIFICATOR: Sending notification ".$notification->id().":".$message);
			$this->env->notificator()->send($notification->getRecipients(), $title, $message);
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