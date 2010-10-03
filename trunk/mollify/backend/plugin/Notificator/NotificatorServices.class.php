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

	class NotificatorServices extends ServicesBase {
		protected function isAdminRequired() { return TRUE; }
		
		protected function isValidPath($method, $path) {
			return count($path) == 1 or count($path) == 2;
		}

		public function processGet() {
			if ($this->path[0] != 'list') throw $this->invalidRequestException();
			
			if (count($this->path) == 1) {
				require_once("dao/NotificatorDao.class.php");
				
				$dao = new NotificatorDao($this->env);
				$this->response()->success($dao->getAllNotifications());
				return;
			}
			if (count($this->path) == 2) {
				require_once("dao/NotificatorDao.class.php");
				
				$dao = new NotificatorDao($this->env);
				$this->response()->success($dao->getNotification($this->path[1]));
				return;
			}

			throw $this->invalidRequestException();
		}
		
		public function processPost() {
			if (count($this->path) != 1 or $this->path[0] != 'list') throw $this->invalidRequestException();
			
			$notification = $this->request->data;
			if (!isset($notification["name"])) throw $this->invalidRequestException("No data");
			
			require_once("dao/NotificatorDao.class.php");
			$dao = new NotificatorDao($this->env);
			$this->response()->success($dao->addNotification($notification));
		}
				
		public function __toString() {
			return "NotificatorServices";
		}
	}
?>