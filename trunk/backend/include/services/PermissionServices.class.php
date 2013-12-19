<?php

	/**
	 * DebugServices.class.php
	 *
	 * Copyright 2008- Samuli Järvelä
	 * Released under GPL License.
	 *
	 * License: http://www.mollify.org/license.php
	 */

	class PermissionServices extends ServicesBase {
		protected function isValidPath($method, $path) {
			return count($path) > 0;
		}
		
		protected function isAuthenticationRequired() {
			return TRUE;
		}
		
		protected function isAdminRequired() {
			return TRUE;
		}

		public function processGet() {
			if ($this->path[0] === 'types') {
				$result = array("types" => $this->env->permissions()->getTypes());
				
				$users = ($this->env->request()->hasParam("u") and strcmp($this->env->request()->param("u"), "1") == 0);
				if ($users) $result["users"] = $this->env->configuration()->getAllUsers(TRUE);
				
				$this->response()->success($result);
				return;
			} else if ($this->path[0] === 'list') {
				$data = $this->request->data;
				$name = $this->env->request()->hasParam("name") ? $this->env->request()->param("name") : NULL;
				$subject = $this->env->request()->hasParam("subject") ? $this->env->request()->param("subject") : NULL;
				$userId = $this->env->request()->hasParam("user_id") ? $this->env->request()->param("user_id") : NULL;
				
				$permissions = $this->env->permissions()->getAllPermissions($name, $subject, $userId);
				$result = array("permissions" => $permissions);
				
				$users = ($this->env->request()->hasParam("u") and strcmp($this->env->request()->param("u"), "1") == 0);
				if ($users) $result["users"] = $this->env->configuration()->getAllUsers(TRUE);
				
				$this->response()->success($result);
				return;
			}
			throw $this->invalidRequestException();
		}
		
		public function processPut() {
			if ($this->path[0] === 'list') {
				$this->response()->success($this->env->permissions()->updatePermissions($this->request->data));
				return;
			}
			throw $this->invalidRequestException();
		}
		
		public function __toString() {
			return "PermissionServices";
		}
	}
?>
