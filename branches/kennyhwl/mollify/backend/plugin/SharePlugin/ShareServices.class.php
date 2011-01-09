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

	class ShareServices extends ServicesBase {
		protected function isValidPath($method, $path) {
			return count($path) == 1 or count($path) == 2;
		}
		
		public function isAuthenticationRequired() {
			return TRUE;
		}
		
		public function processGet() {
			if (count($this->path) != 2 or $this->path[1] != 'info') throw $this->invalidRequestException();

			$item = $this->item($this->path[0]);
			$currentUser = $this->getFolderUser($item);
			if (!$currentUser) throw new ServiceException("REQUEST_FAILED");
			
			$toUserIds = array();
			foreach($this->env->customizations()->getSharedTo($item) as $user) {
				$toUserIds[] = $user["to_user_id"];
			}
			
			$users = $this->env->configuration()->getAllUsers(array("NO", "ST"));
			$available = array();
			foreach($users as $user) {
				if ($user["id"] === $currentUser["id"] or in_array($user["id"], $toUserIds)) continue;
				$available[] = $user;
			}
			$this->response()->success(array("users" => $available));
		}
		
		private function getFolderUser($item) {
			$users = $this->env->configuration()->getFolderUsers($item->rootId());
			if (!$users or count($users) < 1) return FALSE;
			return $users[0];
		}
		
		public function processPost() {
			if (count($this->path) != 1) throw $this->invalidRequestException();		

			$item = $this->item($this->path[0]);			
			$data = $this->request->data;
			
			if (!isset($data['users']) or !isset($data['permission'])) throw $this->invalidRequestException();
			$fromUserId = $this->getUserForItem($item);
			if (!$fromUserId) throw new ServiceException("REQUEST_FAILED", "Could not resolve user");
			
			foreach($data['users'] as $id) {
				$userFolder = $this->getUserFolder($id);
				if (!$userFolder) {
					Logging::logDebug("User folder not found for user ".$id);
					continue;
				}
				
				$target = $this->shareTo($item, $userFolder);
				if ($target != NULL) {
					$p = $data['permission'];
					if (strcasecmp(Authentication::PERMISSION_VALUE_READWRITE, $p) != 0)
						$this->env->configuration()->addItemPermission($target->id(), $p, $id);
					Logging::logDebug("Item ".$item->id()." shared with user ".$id);
				} else {
					Logging::logDebug("Item ".$item->id()." share failed with user ".$id);
				}
				
				$this->addShareToDb($fromUserId, $item, $id, $target);
			}

			$this->response()->success(array());
		}
		
		private function getUserForItem($item) {
			$users = $this->env->configuration()->getFolderUsers($item->rootId());
			if (!$users or count($users) == 0) return NULL;
			
			$user = $users[0];
			return $user["id"];
		}	
	
		private function getUserFolder($userId) {
			$folder = $this->env->customizations()->getUserFolder($userId);
			if ($folder == NULL) return NULL;
			
			$target = $this->env->filesystem()->filesystemFromId($folder["id"]);
			return $target->root();
		}
		
		private function shareTo($item, $to) {
			if ($item->isFile()) $target = $to->fileWithName($item->name(), TRUE);
			else $target = $to->folderWithName($item->name(), TRUE);
			
			$fromPath = $item->internalPath();
			// links are always made as files
			$toPath = rtrim($target->internalPath(), DIRECTORY_SEPARATOR);
			
			Logging::logDebug("Sharing ".$fromPath." => ".$toPath);
			if (!@symlink($fromPath, $toPath)) return NULL;
						
			if (!$item->isFile()) return $to->folderWithName($item->name());
			return $target;
		}

		private function addShareToDb($fromUser, $fromItem, $toUser, $toItem) {
			$db = $this->env->configuration()->db();
			$db->update(sprintf("INSERT INTO ".$db->table("item_share")." (`from_user_id`, `from_item_id`, `to_user_id`, `to_item_id`) VALUES (%s, %s, %s, %s)", $db->string($fromUser, TRUE), $db->string($fromItem->id(), TRUE), $db->string($toUser, TRUE), $db->string($toItem->id(), TRUE)));
		}
		
		public function __toString() {
			return "ShareServices";
		}
	}
?>