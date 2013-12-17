<?php
	/**
	 * PermissionsController.class.php
	 *
	 * Copyright 2008- Samuli Järvelä
	 * Released under GPL License.
	 *
	 * License: http://www.mollify.org/license.php
	 */
	 
	 require_once("PermissionsDao.class.php");

	class Mollify_PermissionsController {
		private $env;
		private $dao;
		private $genericPermissions = array();
		private $filesystemPermissions = array();
		
		//private $filesystemPermissionParentCaches = array();
		private $permissionCaches = array();

		public function __construct($env) {
			$this->env = $env;
			$this->dao = new Mollify_PermissionsDao($this->env->db());
		}
		
		public function registerFilesystemPermission($name, $values = NULL) {
			$this->filesystemPermissions[$name] = $values;
		}
		
		private function getFromCache($name, $subject) {
			if (array_key_exists($name, $this->permissionCaches) and array_key_exists($subject, $this->permissionCaches[$name])) {
				$permission = $this->permissionCaches[$name][$subject];
				Logging::logDebug("Permission cache get [".$name."/".$subject."]=".$permission);
				return $permission;
			}
			return FALSE;			
		}
		
		private function putToCache($name, $subject, $value) {
			if (!array_key_exists($name, $this->permissionCaches)) $this->permissionCaches[$name] = array();			
			$this->permissionCaches[$name][$subject] = $value;
			Logging::logDebug("Permission cache put [".$name."/".$subject."]=".$value);
		}
		
		public function getFilesystemPermission($name, $item) {
			if (!array_key_exists($name, $this->filesystemPermissions)) throw new ServiceException("INVALID_CONFIGURATION", "Invalid permission key: ".$name);
			if ($this->env->authentication()->isAdmin()) {
				$values = $this->filesystemPermissions[$name];
				if ($values != NULL) return $values[count($values)-1];
				return TRUE;
			}
			
			$id = $item->id();
			$permission = $this->getFromCache($name, $id);
			if ($permission !== FALSE) return $permission;

			$permission = $this->dao->getFilesystemPermission($name, $item, $this->env->session()->userId(), $this->getGroupIds());
			if ($permission == NULL) {
				$values = $this->filesystemPermissions[$name];
				if ($values != NULL) $permission = $values[0];	//fallback to first
			}
			$this->putToCache($name, $id, $permission);
			
			return $permission;
		}
		
		private function getGroupIds() {
			$groupIds = array();
			if ($this->env->session()->hasUserGroups()) {
				foreach($this->env->session()->userGroups() as $g)
					$groupIds[] = $g['id'];
			}
			return $groupIds;
		}
		
		public function hasFilesystemRights($name, $item, $value = NULL) {
			if (!array_key_exists($name, $this->filesystemPermissions)) throw new ServiceException("INVALID_CONFIGURATION", "Invalid permission key: ".$name);
			$values = $this->filesystemPermissions[$name];
			if ($value != NULL and $values != NULL) {
				$valueIndex = array_search($value, $values);
				if ($valueIndex === FALSE)
					throw new ServiceException("INVALID_CONFIGURATION", "Invalid permission value: ".$value);
			}
			
			if ($this->env->authentication()->isAdmin()) return TRUE;
			
			$userValue = $this->getFilesystemPermission($name, $item);
			if (!$userValue) return FALSE;
			
			// on/off permission is found
			if ($values == NULL) return TRUE;
			
			$userValueIndex = array_search($userValue, $values);
			if ($userValueIndex === FALSE)
				throw new ServiceException("INVALID_CONFIGURATION", "Invalid permission value: ".$userValue);
				
			// check permission level by index
			return $userValueIndex >= $valueIndex;
		}
		
		public function prefetchFilesystemChildrenPermissions($name, $parent) {
			if ($this->env->authentication()->isAdmin()) return;
			
			$permissions = $this->dao->getFilesystemPermissionsForChildren($name, $parent, $this->env->session()->userId(), $this->getGroupIds());

			//TODO $this->filesystemPermissionParentCaches[] = $folder->id();
			if (!array_key_exists($name, $this->permissionCaches)) $this->permissionCaches[$name] = array();
			foreach($permissions as $id => $p)
				$this->permissionCaches[$name][$id] = $p;
		}
		
		/*public function permission($item) {
			if (!$item) return Authentication::PERMISSION_VALUE_NO_RIGHTS;
			if ($this->env->authentication()->isAdmin()) return Authentication::PERMISSION_VALUE_READWRITE;
			
			$permission = $this->getItemUserPermission($item);
			if (!$permission) return $this->env->authentication()->getDefaultPermission();
			return $permission;
		}
		

		
		public function temporaryItemPermission($item, $permission) {
			$this->permissionCache[$item->id()] = $permission;
		}
		
		private function getItemUserPermission($item) {
			if (array_key_exists($item->id(), $this->permissionCache)) {
				$permission = $this->permissionCache[$item->id()];
				Logging::logDebug("Permission cache get [".$item->id()."]=".$permission);
			} else {
				$permission = $this->env->permissions()->getFilesystemPermission("filesystem_item_access", $item);
				//Logging::logDebug("ITEM PERMISSION: ".$this->env->permissions()->getFilesystemPermission("filesystem_item_access", $item));
				if (!$permission) return $this->env->authentication()->getDefaultPermission();
				
				$this->permissionCache[$item->id()] = $permission;
				Logging::logDebug("Permission cache put [".$item->id()."]=".$permission);
			}
			return $permission;
		}

		private function getItemUserPermissionFromCache($item) {
			if (array_key_exists($item->id(), $this->permissionCache)) {
				$permission = $this->permissionCache[$item->id()];
				Logging::logDebug("Permission cache get [".$item->id()."]=".$permission);
			} else {
				$parentId = $item->parent()->id();
				if ($item->isFile() and array_key_exists($parentId, $this->permissionCache)) {
					$permission = $this->permissionCache[$parentId];
					Logging::logDebug("Permission cache get [".$item->id()."->".$parentId."]=".$permission);
				} else {
					return $this->env->authentication()->getDefaultPermission();
				}
			}
			return $permission;
		}
		
		public function allPermissions($item) {
			Logging::logDebug("ITEM PERMISSION: ".$this->env->permissions()->getFilesystemPermission("filesystem_item_access", $item));
			return $this->env->configuration()->getItemPermissions($item);
		}*/

		public function removeFilesystemPermissions($item, $name = NULL) {
			$this->dao->removeFilesystemPermissions($name, $item);
		}
	}
?>