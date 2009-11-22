<?php
	class ConfigurationProvider {
		function initializeSession($session, $userId) {
			$session->setSessionParam('default_file_permission', $this->getDefaultUserPermissionMode($userId));
			$session->setSessionParam('roots', $this->getUserRootDirectories($userId));
		}
		
		public function checkProtocolVersion($version) {}
		
		public function getSupportedFeatures() {
			return array();
		}
		
		public function onSessionStart($userId, $username) {
			return TRUE;
		}
	
		function findUser($username, $password) {
			return FALSE;
		}
		
		function getAllUsers() {
			return array();
		}

		function getUser($id) {
			return FALSE;
		}
		
		function getDefaultPermissionMode($userId = "") {
			return FALSE;
		}
	
		function isAuthenticationRequired() {
			return TRUE;
		}
		
		public function getUserRootDirectories($userId) {
			return array();
		}
		
		function getFileDescription($file) {
			return NULL;
		}
		
		function getDirDescription($dir) {
			return NULL;
		}
		
		function setItemDescription($item, $description) {
			return FALSE;
		}
	
		function removeItemDescription($item, $recursively = FALSE) {
			return FALSE;
		}
		
		function moveItemDescription($from, $to, $recursively = FALSE) {
			return FALSE;
		}
					
		function getItemPermission($item, $userId) {
			return FALSE;
		}
	
		function getItemPermissions($item) {
			return FALSE;
		}
			
		function updateItemPermissions($new, $modified, $removed) {
			return FALSE;
		}

		function removeAllItemPermissions($item, $recursively = FALSE) {
			return FALSE;
		}
		
		function moveItemPermissions($from, $to, $recursively = FALSE) {
			return FALSE;
		}
		
		function log() {
			Logging::logDebug("CONFIGURATION PROVIDER (".get_class($this)."): supported features=".Util::array2str($this->getSupportedFeatures())." auth=".$this->isAuthenticationRequired());
		}
	}
?>