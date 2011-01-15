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

	class W3HubCustomizations {
		static $FOLDER_PATHS = "/foo/kennyhwl/users/";
		static $INBOX_NAME = "Inbox";
		
		protected $env;
		
		public function __construct() {
		}
		
		public function initialize($env) {
			$this->env = $env;
			$this->env->filesystem()->registerDetailsPlugin($this);
			$this->env->events()->register("filesystem/", $this);
			$this->env->plugins()->addPlugin("SharePlugin", array());
			$this->env->plugins()->addPlugin("Guest", array());
		}
		
		public function getInboxPath() {
			return self::$INBOX_NAME.DIRECTORY_SEPARATOR;
		}
		
		public function onUserAdded($id, $user) {
			if (strtoupper($user['permission_mode']) !== 'NO' and strtoupper($user['permission_mode']) !== 'ST') return;
			
			$folderName = $user["name"];
			$folderPath = self::$FOLDER_PATHS.$id;
			
			mkdir($folderPath);
			mkdir($folderPath.DIRECTORY_SEPARATOR.self::$INBOX_NAME);
			
			$folderId = $this->env->configuration()->addFolder($folderName, $folderPath);
			$this->env->configuration()->addUserFolder($id, $folderId, NULL);
			
			$fs = $this->env->filesystem()->filesystem(array("id" => $folderId, "path" => $folderPath, "name" => $folderName), FALSE);
			$this->env->configuration()->addItemPermission($fs->root()->id(), Authentication::PERMISSION_VALUE_READWRITE, $id);
		}
		
		public function onUserRemoved($user) {
			if (strtoupper($user['permission_mode']) !== 'NO' and strtoupper($user['permission_mode']) !== 'ST') return;
			$folder = $this->getUserFolder($user["id"]);
			if ($folder) {
				$root = $this->env->filesystem()->item($folder["id"].":/");
				$this->onBeforeDelete($root);
				$root->delete();
				
				$db = $this->env->configuration()->db();
				$db->update(sprintf("DELETE FROM ".$db->table("folder")." WHERE id='%s'", $db->string($folder["id"])));
			}
		}

		public function onUserRenamed($userId, $name) {
			$folder = $this->getUserFolder($userId);
			if (!$folder) return;
			
			$db = $this->env->configuration()->db();
			$db->update(sprintf("UPDATE ".$db->table("folder")." SET name='%s' WHERE id='%s'", $db->string($name), $db->string($folder["id"])));
			
			$this->env->configuration()->updateUserFolder($userId, $folder["id"], $name);
		}
		
		public function getUserFolder($userId) {
			$folders = $this->env->configuration()->getUserFolders($userId);
			if (!$folders or count($folders) == 0) return NULL;
			
			return $folders[0];
		}
		
		public function getFolderUser($item) {
			$users = $this->env->configuration()->getFolderUsers($item->rootId());
			if (!$users or count($users) < 1) return FALSE;
			return $users[0];
		}
		
		public function onEvent($e) {
			if ($e->subType() === FileEvent::UPLOAD) {
				$guestUpload = (strpos($this->env->request()->URI(), "guest/") === 0);
				$item = $e->item();
				Logging::logDebug("Customization: File uploaded, guest: ".($guestUpload ? "yes" : "no"));

				$size = $this->getItemSize($item);
				if (!$guestUpload) {
					$available = $this->getAvailableQuota($item);
					if ($available === FALSE) return;	// no quota set	

					if ($size > $available) {
						unlink($item->internalPath());
						throw new ServiceException("QUOTA_EXCEEDED", "Quota available ".$available.", required ".$size);
					}
				}
				
				$this->removeQuota($item, $size);
				
				if ($guestUpload) {
					$user = $this->getFolderUser($item);
					if ($user and isset($user["email"]) and strlen($user["email"]) > 0) {
						require_once("include/customizations/Messages.php");
						$msg = Util::replaceParams($GUEST_UPLOAD_NOTIFICATION_MESSAGE, array("name" => $item->name()));
						$recipient = array(array("email" => $user["email"]));
			
						$this->env->notificator()->send($recipient, $GUEST_UPLOAD_NOTIFICATION_SUBJECT, $msg);
					}
				}
				return;
			}
			if ($e->subType() !== FileEvent::RENAME and $e->subType() !== FileEvent::MOVE) return;
			
			$item = $e->item();
			$shared = $this->isShared($item);
			if (!$shared) return;
			
			$db = $this->env->configuration()->db();
			$id = $db->string($item->id());
			
			$toId = $db->string($e->info()->id());
			$toItem = $this->env->filesystem()->item($toId);
			
			if ($shared === 'FROM') {
				foreach($this->getSharedTo($item) as $to) {
					$copy = $this->env->filesystem()->item($to["to_item_id"], TRUE);
					$copyPath = rtrim($copy->internalPath(), DIRECTORY_SEPARATOR);

					// TODO if copy name needs to change as well, all permissions, descriptions etc must be renamed as well
					// but in this case user should not be able to rename the shared copy at all

					//$toItem->isFile() ? $toItem->parent()->fileWithName($e->info()->name()) : $toItem->parent()->folderWithName($e->info()->name());
					
					if (file_exists($copyPath)) {
						Logging::logDebug("Could not read shared copy, item with same name exists already:".$copyPath);
					} else {
						if (!symlink($toItem->internalPath(), $copyPath))
							Logging::logDebug("Reading shared copy failed: ".$copyPath);
					}
				}

				$db->update(sprintf("UPDATE ".$db->table("item_share")." SET from_item_id='%s' where from_item_id='%s'", $toId, $id));
			} else {
				$db->update(sprintf("UPDATE ".$db->table("item_share")." SET to_item_id='%s' where to_item_id='%s'", $toId, $id));
			}
		}
		
		public function getAvailableQuota($item) {
			$folder = $this->env->configuration()->getFolder($item->rootId());
			if ($folder["quota"] === "0" or $folder["quota"] === 0) return FALSE;
			return $folder["quota"] - $folder["quota_used"];
		}
		
		public function onBeforeCopy($item, $to) {
			$available = $this->getAvailableQuota($item);
			if ($available === FALSE) return;	// no quota set
			
			$size = $this->getItemSize($item);
			if ($size > $available) throw new ServiceException("QUOTA_EXCEEDED", "Quota available ".$available.", required ".$size);
			
			$this->removeQuota($item, $size);
		}
		
		public function onBeforeDelete($item) {
			$this->restoreQuota($item, $this->getItemSize($item));
			
			$shared = $this->isShared($item);
			if (!$shared) {
				if (!$item->isFile())
					$this->deleteAllPossibleChildShares($item);
				return;
			}

			if ($shared === 'FROM')
				$this->deleteAllSharedCopies($item);
			
			$db = $this->env->configuration()->db();
			$id = $db->string($item->id());	
			$db->update(sprintf("DELETE FROM ".$db->table("item_share")." WHERE from_item_id='%s' OR to_item_id='%s'", $id, $id));
		}
		
		public function refreshUsedQuota($id) {
			$root = $this->env->filesystem()->item($id.":/");
			$size = $this->getItemSize($root);
			Logging::logDebug("Quota used for ".$id.": ".$size);
			$db = $this->env->configuration()->db();
			$db->update(sprintf("UPDATE ".$db->table("folder")." SET quota_used=%s WHERE id='%s' and quota > 0", $size, $db->string($root->id())));
		}
		
		public function getQuotaInfo($item) {
			$folder = $this->env->configuration()->getFolder($item->rootId());
			if ($folder["quota"] === "0" or $folder["quota"] === 0) return array("quota" => 0, "used" => 0);
			return array("quota" => $folder["quota"], "used" => $folder["quota_used"]);
		}
		
		public function getItemSize($item) {
			if ($item->isFile()) return $item->size();
			$excluded = $this->getAllSharedItemsInRoot($item->rootId());
			return $this->folderSize($item->internalPath(), $excluded);
		}
		
		private function getAllSharedItemsInRoot($rootId) {
			$folder = $this->env->configuration()->getFolder($rootId);
			$path = rtrim($folder["path"], DIRECTORY_SEPARATOR).DIRECTORY_SEPARATOR;
			
			$paths = array();
			$db = $this->env->configuration()->db();	
			foreach($db->query("SELECT to_item_id FROM ".$db->table("item_share")." where to_item_id REGEXP '^".$rootId."*'")->rows() as $row)
				$paths[] = str_replace($rootId, $path, $row["to_item_id"]);
			
			//Logging::logDebug("Exclude: ".Util::array2str($paths));
			return $paths;
		}
		
		private function folderSize($path, $excluded) {
			$files = scandir($path);
			if (!$files) throw new ServiceException("INVALID_PATH", $path);
			
			$size = 0;
			
			foreach($files as $i => $name) {
				if (substr($name, 0, 1) == '.')
					continue;
	
				$fullPath = rtrim($path, DIRECTORY_SEPARATOR).DIRECTORY_SEPARATOR.$name;
				
				if (in_array($fullPath, $excluded)) {
					continue;
				}
				if (is_dir($fullPath)) {
					$size = $size + $this->folderSize($fullPath, $excluded);
				} else {
					$size = $size + filesize($fullPath);
				}
			}
			return $size;
		}

		public function removeQuota($item, $amount) {
			$db = $this->env->configuration()->db();
			$db->update(sprintf("UPDATE ".$db->table("folder")." SET quota_used=(quota_used+%s) WHERE id='%s' and quota > 0", $amount, $db->string($item->rootId())));
		}
				
		public function restoreQuota($item, $amount) {
			$db = $this->env->configuration()->db();
			$db->update(sprintf("UPDATE ".$db->table("folder")." SET quota_used=GREATEST(0, quota_used-%s) WHERE id='%s' and quota > 0", $amount, $db->string($item->rootId())));
		}
		
		private function deleteAllPossibleChildShares($item) {
			$db = $this->env->configuration()->db();
			
			foreach($db->query("SELECT distinct from_item_id FROM ".$db->table("item_share")." where from_item_id REGEXP '^".$item->id()."*'")->rows() as $from) {
				$fromItem = $this->env->filesystem()->item($from["from_item_id"]);
				$this->deleteAllSharedCopies($fromItem);
			}
			$db->update("DELETE FROM ".$db->table("item_share")." WHERE from_item_id like '".$item->id()."%' or to_item_id like '".$item->id()."%'");
		}
		
		private function deleteAllSharedCopies($item) {
			Logging::logDebug("Removing shared copies of ".$item->id());
			
			$ids = array();
			foreach($this->getSharedTo($item) as $to) {
				$id = $to["to_item_id"];
				$ids[] = $id;
				$copy = $this->env->filesystem()->item($id);
				
				$path = rtrim($copy->internalPath(), DIRECTORY_SEPARATOR);
				unlink($path);
			}
			
			if (count($ids) > 0) {
				$db = $this->env->configuration()->db();
				$db->update("DELETE FROM ".$db->table("item_permission")." where item_id in (".$db->arrayString($ids, TRUE).")");
			}
		}

		public function onBeforeRename($item) {
			$shared = $this->isShared($item);
			if (!$shared) return;

			// remove symbolic links before rename, and add new ones afterwards
			if ($shared === 'FROM')
				$this->deleteAllSharedCopies($item);
		}
		
		public function onBeforeMove($item) {
			$shared = $this->isShared($item);
			if (!$shared) return;

			// remove symbolic links before rename, and add new ones afterwards
			if ($shared === 'FROM')
				$this->deleteAllSharedCopies($item);
		}

		public function isProtected($item) {
			return ($item->path() === $this->getInboxPath());
		}

		public function isShared($item) {
			$id = $item->id();
			
			$db = $this->env->configuration()->db();
			$from = $db->query("SELECT count(to_user_id) FROM ".$db->table("item_share")." where from_item_id = '".$id."'")->value(0) > 0;
			if ($from) return "FROM";
			
			$to = $db->query("SELECT count(from_user_id) FROM ".$db->table("item_share")." where to_item_id = '".$id."'")->value(0) > 0;
			if ($to) return "TO";
			
			$paths = split(DIRECTORY_SEPARATOR, $item->path());
			$current = $item->rootId();
			$count = 0;
			$a = array();
			foreach($paths as $p) {
				if ($count >= count($paths) - 1) break;
				$current .= $p.DIRECTORY_SEPARATOR;
				$count = $count + 1;
				$a[] = $current;
			}
			
			if (count($a) > 0) {
				$to = $db->query("SELECT count(from_user_id) FROM ".$db->table("item_share")." where to_item_id in (".$db->arrayString($a, TRUE).")")->value(0) > 0;
				if ($to) return "TO_CHILD";
			}
			
			return NULL;
		}
		
		public function getSharedTo($item) {
			$db = $this->env->configuration()->db();
			return $db->query("SELECT to_item_id, to_user_id FROM ".$db->table("item_share")." where from_item_id = '".$item->id()."'")->rows();
		}

		public function getSharingInfo($item) {
			$id = $item->id();
			
			$result = array();
			$db = $this->env->configuration()->db();
			
			$from = array();
			foreach($db->query("SELECT from_item_id FROM ".$db->table("item_share")." where from_item_id REGEXP '^".$id."*'")->rows() as $row)
				$from[] = base64_encode($row["from_item_id"]);
			$result["from"] = $from;
			
			$to = array();
			foreach($db->query("SELECT to_item_id FROM ".$db->table("item_share")." where to_item_id REGEXP '^".$id."*'")->rows() as $row)
				$to[] = base64_encode($row["to_item_id"]);
			$result["to"] = $to;
			
			return $result;
		}
		
		public function getItemDetails($item) {
			return array("protected" => $this->isProtected($item), "shared" => $this->isShared($item));
		}
		
		public function __toString() {
			return "W3HubCustomizations";
		}
	}
?>