<?php

	/**
	 * Copyright (c) 2008- Samuli Järvelä
	 *
	 * All rights reserved. This program and the accompanying materials
	 * are made available under the terms of the Eclipse Public License v1.0
	 * which accompanies this distribution, and is available at
	 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
	 * this entire header must remain intact.
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
			$this->sendNotifications($notifications);
		}
		
		private function findNotifications($type, $userId) {
			require_once("dao/NotificatorDao.class.php");
			
			$dao = new NotificatorDao($this->env);
			return $dao->findNotifications($type, $userId);
		}

		private function sendNotifications($notifications) {
			Logging::logDebug("NOTIFICATOR: Sending ".count($notifications)." notifications");
			
			foreach($notifications as $notification)
				$this->sendNotification($notification);
		}

		private function sendNotification($notification) {
			Logging::logDebug("NOTIFICATOR: Sending notification ".$notification);
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