<?php

	/**
	 * ShareHandler.class.php
	 *
	 * Copyright 2008- Samuli Järvelä
	 * Released under GPL License.
	 *
	 * License: http://www.mollify.org/license.php
	 */
	
	require_once("dao/ShareDao.class.php");
	
	class ShareHandler {
		private $env;
		private $settings;
		private $customShareHandlers;
		
		public function __construct($env, $settings) {
			$this->env = $env;
			$this->settings = $settings;
			$this->customShareHandlers = array();
		}

		public function registerHandler($type, $h) {
			$this->customShareHandlers[$type] = $h;
		}
				
		public function getItemContextData($item, $details, $key, $data) {
			$list = $this->getShareUsers($item);
			$users = Util::arrayCol($list, "user_id");
			$count = count($users);
			$own = FALSE;
			$others = FALSE;
			if ($count > 0) {
				$own = in_array($this->env->session()->userId(), $users);
				$others = ($count - ($own  ? 1 : 0) > 0);
			}
			
			return array(
				"count" => $own ? $this->dao()->getShareCount($item, $this->env->session()->userId()) : 0,
				"other_users" => $others
			);
		}
		
		public function getRequestData($parent, $items, $key, $dataRequest) {
			if ($parent != NULL)
				return $this->dao()->getUserSharesForChildren($parent, $this->env->session()->userId());
			//TODO each item
			return $this->dao()->getUserSharesForItems($items, $this->env->session()->userId());
		}
		
		public function validateAction($action, $target, $acceptKeys) {
			if (FileEvent::DELETE != $action || !$target) return;
			
			$shareList = $this->getShareUsers($target);
			$usersByItemId = array();
			$itemId = "";
			foreach($shareList as $r) {
				if (strcmp($r["item_id"], $itemId) != 0) {
					$itemId = $r["item_id"];
					$usersByItemId[$itemId] = array();
				}
				$usersByItemId[$itemId][] = $r["user_id"];
			}
			$list = array();
			
			foreach($usersByItemId as $itemId => $users) {
				$count = count($users);
				if ($count == 0) continue;

				$own = in_array($this->env->session()->userId(), $users);
				$others = ($count - ($own ? 1 : 0) > 0);
				
				$sharedOwnKey = "item_shared-".$itemId;
				$sharedOthersKey = "item_shared_others-".$itemId;

				if ($own && !in_array($sharedOwnKey, $acceptKeys)) {
					$list[] = array("item" => $itemId, "reason" => "item_shared", "acceptable" => TRUE, "acceptKey" => $sharedOwnKey);
				}
				if ($others && (!$this->env->authentication()->isAdmin() || !in_array($sharedOthersKey, $acceptKeys))) {
					$list[] = array("item" => $itemId, "reason" => "item_shared_others", "acceptable" => $this->env->authentication()->isAdmin(), "acceptKey" => $sharedOthersKey);
				}
			}
			return $list;
		}

		public function getUserShares() {
			return $this->dao()->getUserShares($this->env->session()->userId());
		}
		
		public function getShares($itemId) {
			return $this->dao()->getShares($itemId, $this->env->session()->userId());
		}

		public function getShareUsers($i) {
			return $this->dao()->getShareUsers($i);
		}
		
		public function addShare($item, $name, $expirationTs, $active) {
			$created = $this->env->configuration()->formatTimestampInternal(time());
			$this->dao()->addShare($this->GUID(), $item, $name, $this->env->session()->userId(), $expirationTs, $created, $active);
		}

		public function editShare($id, $name, $expirationTs, $active) {
			$this->dao()->editShare($id, $name, $expirationTs, $active);
		}
		
		public function deleteShare($id) {
			$this->dao()->deleteShare($id);
		}
		
		public function deleteSharesForItem($itemId) {
			$this->dao()->deleteSharesForItem($itemId);
		}
		
		public function getPublicShareInfo($id) {
			$share = $this->dao()->getShare($id, $this->env->configuration()->formatTimestampInternal(time()));
			if (!$share) return NULL;
			
			return $this->doGetSharePublicInfo($share);
		}
		
		private function doGetSharePublicInfo($share) {
			$itemId = $share["item_id"];
			$type = NULL;
			$name = NULL;
			
			if (strpos($itemId, "_") > 0) {
				$parts = explode("_", $itemId);
				$info = $this->getCustomShareInfo($parts[0], $parts[1], $share);
				if ($info == NULL) return NULL;
				
				$type = $info["type"];
				$name = $info["name"];
			} else {
				$this->env->filesystem()->allowFilesystems = TRUE;
				$item = $this->env->filesystem()->item($itemId);
				$type = $item->isFile() ? "download" : "upload";
				$name = $item->name();
			}
			
			//TODO processed download
			//TODO needs auth/password?
			return array("type" => $type, "name" => $name, "auth" => FALSE, "pw" => FALSE);			
		}
		
		private function getCustomShareInfo($type, $id, $share) {
			if(!array_key_exists($type, $this->customShareHandlers)) {
				Logging::logError("No custom share handler found: ".$type);
				return NULL;
			}
			$handler = $this->customShareHandlers[$type];
			return $handler->getShareInfo($id, $share);
		}
		
		public function processShareGet($id, $params) {
			$share = $this->dao()->getShare($id, $this->env->configuration()->formatTimestampInternal(time()));
			if (!$share) throw new ServiceException("INVALID_REQUEST");

			$itemId = $share["item_id"];
			if (strpos($itemId, "_") > 0) {
				$parts = explode("_", $itemId);
				$this->processCustomGet($parts[0], $parts[1], $share, $params);
				return;
			}
			
			$this->env->filesystem()->allowFilesystems = TRUE;
			$item = $this->env->filesystem()->item($itemId);
			if (!$item) throw new ServiceException("INVALID_REQUEST");
			if (!$item->isFile()) throw new ServiceException("INVALID_REQUEST");

			$this->processDownload($item);
		}

		public function processSharePrepareGet($id) {
			$share = $this->dao()->getShare($id, $this->env->configuration()->formatTimestampInternal(time()));
			if (!$share) throw new ServiceException("INVALID_REQUEST");
			
			$info = $this->doGetSharePublicInfo($share);
			if ($info == NULL or $info["type"] != "prepared_download") throw new ServiceException("INVALID_REQUEST");

			$itemId = $share["item_id"];
			if (strpos($itemId, "_") > 0) {
				$parts = explode("_", $itemId);
				return $this->processCustomPrepareGet($parts[0], $parts[1], $share);
			}
			
			//TODO zip download
			throw new ServiceException("INVALID_REQUEST");
			
			$this->env->filesystem()->allowFilesystems = TRUE;
			$item = $this->env->filesystem()->item($itemId);
			if (!$item) throw new ServiceException("INVALID_REQUEST");
			if ($item->isFile()) throw new ServiceException("INVALID_REQUEST");

			//prepare zip for folder
		}
				
		private function processCustomGet($type, $id, $share, $params) {
			if(!array_key_exists($type, $this->customShareHandlers)) {
				Logging::logError("No custom share handler found: ".$type);
				die();
			}
			$handler = $this->customShareHandlers[$type];
			$handler->processGetShare($id, $share, $params);
		}

		private function processCustomPrepareGet($type, $id, $share) {
			if(!array_key_exists($type, $this->customShareHandlers)) {
				Logging::logError("No custom share handler found: ".$type);
				die();
			}
			$handler = $this->customShareHandlers[$type];
			return $handler->processPrepareGetShare($id, $share);
		}
		
		private function processDownload($file) {
			$mobile = ($this->env->request()->hasParam("m") and strcmp($this->env->request()->param("m"), "1") == 0);
			
			$this->env->filesystem()->temporaryItemPermission($file, Authentication::PERMISSION_VALUE_READONLY);
			$this->env->filesystem()->download($file, $mobile);
		}
		
		public function processSharePost($id) {
			$share = $this->dao()->getShare($id);
			if (!$share) throw new ServiceException("INVALID_REQUEST");
			
			$this->env->filesystem()->allowFilesystems = TRUE;
			$item = $this->env->filesystem()->item($share["item_id"]);
			if (!$item or $item->isFile()) throw new ServiceException("INVALID_REQUEST");

			$this->processUpload($id, $item);
		}

		public function processUpload($shareId, $folder) {
			$this->env->filesystem()->temporaryItemPermission($folder, Authentication::PERMISSION_VALUE_READWRITE);
			$this->env->filesystem()->uploadTo($folder);
		}
								
		public function onEvent($e) {
			if (strcmp(FilesystemController::EVENT_TYPE_FILE, $e->type()) != 0) return;
			$type = $e->subType();
			
			if ($type === FileEvent::DELETE)
				$this->dao()->deleteShares($e->item());
		}
		
		private function dao() {
			return new ShareDao($this->env);
		}
		
		private function GUID() {
			if (function_exists('com_create_guid') === true)
				return str_replace('-', '', trim(com_create_guid(), '{}'));
			return sprintf('%04X%04X%04X%04X%04X%04X%04X%04X', mt_rand(0, 65535), mt_rand(0, 65535), mt_rand(0, 65535), mt_rand(16384, 20479), mt_rand(32768, 49151), mt_rand(0, 65535), mt_rand(0, 65535), mt_rand(0, 65535));
		}
				
		public function __toString() {
			return "ShareHandler";
		}
	}
?>
