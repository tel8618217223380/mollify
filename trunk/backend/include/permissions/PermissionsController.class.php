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
		//TODO cache
		
		private $env;
		private $dao;
		private $genericPermissions = array();
		private $filesystemPermissions = array();

		public function __construct($env) {
			$this->env = $env;
			$this->dao = new Mollify_PermissionsDao($this->env->db());
		}
		
		public function registerFilesystemPermission($name, $values = NULL) {
			$this->filesystemPermissions[$name] = $values;
		}
		
		public function getFilesystemPermission($name, $item) {
			if (!array_key_exists($name, $this->filesystemPermissions)) throw new ServiceException("INVALID_CONFIGURATION", "Invalid permission key: ".$name);
			$userId = $this->env->session()->userId();
			$groupIds = array();
			if ($this->env->session()->hasUserGroups()) {
				foreach($this->env->session()->userGroups() as $g)
					$groupIds[] = $g['id'];
			}

			return $this->dao->getFilesystemPermission($name, $item, $userId, $groupIds);
		}
	}
?>