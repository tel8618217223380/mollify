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
		
		private $filesystemPermissionPrefetchedParents = array();
		private $permissionCaches = array();

		public function __construct($env) {
			$this->env = $env;
			$this->dao = new Mollify_PermissionsDao($this->env->db());
		}
		
		public function registerFilesystemPermission($name, $values = NULL) {
			$this->filesystemPermissions[$name] = $values;
		}
		
		public function getTypes() {
			return array("generic" => $this->genericPermissions, "filesystem" => $this->filesystemPermissions);
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
		
		public function getAllFilesystemPermissions($item) {
			$result = array();
			foreach($this->filesystemPermissions as $name -> $values) {
				$result[$name] = $this->getFilesystemPermission($name, $item);
			}
			return $result;
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
			
			// if parent folder has been prefetched, we know item does not have specific permissions -> try parent permission
			$parentId = $item->parent()->id();
			if (array_key_exists($name, $this->filesystemPermissionPrefetchedParents) and in_array($parentId, $this->filesystemPermissionPrefetchedParents[$name])) {
				$permission = $this->getFromCache($name, $parentId);
				if ($permission !== FALSE) return $permission;				
			}

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
		
		public function hasFilesystemRights($name, $item, $required = NULL) {
			if (!array_key_exists($name, $this->filesystemPermissions)) throw new ServiceException("INVALID_CONFIGURATION", "Invalid permission key: ".$name);
			$values = $this->filesystemPermissions[$name];
			if ($required != NULL and $values != NULL) {
				$requiredIndex = array_search($required, $values);
				if ($requiredIndex === FALSE)
					throw new ServiceException("INVALID_CONFIGURATION", "Invalid permission value: ".$required);
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
			return $userValueIndex >= $requiredIndex;
		}
		
		public function prefetchFilesystemChildrenPermissions($name, $parent) {
			if ($this->env->authentication()->isAdmin()) return;
			
			$permissions = $this->dao->getFilesystemPermissionsForChildren($name, $parent, $this->env->session()->userId(), $this->getGroupIds());

			if (!array_key_exists($name, $this->filesystemPermissionPrefetchedParents)) $this->filesystemPermissionPrefetchedParents[$name] = array();
			$this->filesystemPermissionPrefetchedParents[$name][] = $folder->id();
			
			if (!array_key_exists($name, $this->permissionCaches)) $this->permissionCaches[$name] = array();
			foreach($permissions as $id => $p)
				$this->permissionCaches[$name][$id] = $p;
		}
		
		public function temporaryFilesystemPermission($name, $item, $permission) {
			$this->permissionCaches[$name][$item->id()] = $permission;
		}
		
		public function getPermissions($name = NULL, $subject = NULL, $userId = NULL) {			
			if ($name != NULL) {
				if (!array_key_exists($name, $this->genericPermissions) and !array_key_exists($name, $this->filesystemPermissions))
					throw new ServiceException("INVALID_CONFIGURATION", "Invalid permission key: ".$name);
			}
			if ($userId == $this->env->session()->userId() and $this->env->authentication()->isAdmin()) return array();
			
			return $this->dao->getPermissions($name, $subject, $userId);
		}
		
		public function getGenericPermissions($name = NULL, $userId = NULL) {
			if ($name != NULL) {
				if (!array_key_exists($name, $this->genericPermissions) and !array_key_exists($name, $this->filesystemPermissions))
					throw new ServiceException("INVALID_CONFIGURATION", "Invalid permission key: ".$name);
			}
			if ($userId == $this->env->session()->userId() and $this->env->authentication()->isAdmin()) return array();
			
			return $this->dao->getGenericPermissions($name, $userId);
		}
		
		public function updatePermissions($permissionData) {
			//TODO validate			
			return $this->dao->updatePermissions($permissionData);
		}

		public function removeFilesystemPermissions($item, $name = NULL) {
			$this->dao->removeFilesystemPermissions($name, $item);
		}
		
		public function processQuery($data) {
			return $this->dao->processQuery($data);
		}
		
		public function getSessionInfo() {
			$result = array();
			$result["permissions"] = $this->getGenericPermissions(NULL, $this->env->session()->userId());
			return $result;
		}
	}
?>