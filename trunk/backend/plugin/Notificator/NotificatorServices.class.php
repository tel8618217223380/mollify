<?php

	/**
	 * NotificatorServices.class.php
	 *
	 * Copyright 2008- Samuli Järvelä
	 * Released under GPL License.
	 *
	 * License: http://www.mollify.org/license.php
	 */

	class NotificatorServices extends ServicesBase {
		protected function isAdminRequired() { return TRUE; }
		
		protected function isValidPath($method, $path) {
			return TRUE;
		}

		public function processGet() {
			if ($this->path[0] != 'list') throw $this->invalidRequestException();
			
			if (count($this->path) == 1) {
				$dao = $this->getDao();
				$this->response()->success($dao->getAllNotifications());
				return;
			}
			if (count($this->path) == 2) {
				$dao = $this->getDao();
				$this->response()->success($dao->getNotification($this->path[1]));
				return;
			}

			throw $this->invalidRequestException();
		}
		
		public function processPost() {
			if (count($this->path) != 1 or $this->path[0] != 'list') throw $this->invalidRequestException();
			
			$notification = $this->request->data;
			if (!isset($notification["name"])) throw $this->invalidRequestException("No data");
			
			$dao = $this->getDao();
			$this->response()->success($dao->addNotification($notification));
		}

		public function processDelete() {
			if (count($this->path) < 2 or count($this->path) > 3 or $this->path[0] != 'list') throw $this->invalidRequestException("Invalid path");
			
			$id = $this->path[1];
			$dao = $this->getDao();
			if (count($this->path) == 2) {
				$this->response()->success($dao->removeNotification($id));
				return;
			}
			
			$key = $this->path[2];
			$data = $this->request->data;
			if (!isset($data["ids"]) or !is_array($data["ids"])) throw $this->invalidRequestException("no ids");
			$ids = $data["ids"];
			if (count($ids) == 0) throw $this->invalidRequestException("no ids");
			
			if ($key == "users") {
				$this->response()->success($dao->removeNotificationUsers($id, $ids));
				return;
			}
			if ($key == "recipients") {
				$this->response()->success($dao->removeNotificationRecipients($id, $ids));
				return;
			}
			if ($key == "events") {
				$this->response()->success($dao->removeNotificationEvents($id, $ids));
				return;
			}

			throw $this->invalidRequestException();
		}
		
		public function processPut() {
			if (count($this->path) != 2 or $this->path[0] != 'list') throw $this->invalidRequestException();
			
			$id = $this->path[1];
			$data = $this->request->data;
			$dao = $this->getDao();
			
			if (isset($data["name"])) {
				$this->response()->success($dao->editNotificationName($id, $data["name"]));
				return;
			}
			if (isset($data["message"])) {
				$this->response()->success($dao->editNotificationMessage($id, isset($data["message_title"]) ? $data["message_title"] : "", $data["message"]));
				return;
			}
			if (isset($data["events"])) {
				$this->response()->success($dao->editNotificationEvents($id, $data["events"]));
				return;
			}
			if (isset($data["users"])) {
				$this->response()->success($dao->editNotificationUsers($id, $data["users"]));
				return;
			}
			if (isset($data["recipients"])) {
				$this->response()->success($dao->editNotificationRecipients($id, $data["recipients"]));
				return;
			}
						
			throw $this->invalidRequestException();
		}
				
		private function getDao() {
			require_once("dao/NotificatorDao.class.php");
			return new NotificatorDao($this->env);
		}
				
		public function __toString() {
			return "NotificatorServices";
		}
	}
?>