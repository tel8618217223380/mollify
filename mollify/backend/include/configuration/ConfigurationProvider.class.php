<?php
	class ConfigurationProvider {
		protected $env;
		
		function initialize($request, $env) {
			$this->env = $env;
		}
		
		function initializeSession($session, $userId) {}
		
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
		
		function getDefaultPermission($userId = "") {
			return FALSE;
		}
	
		function isAuthenticationRequired() {
			return TRUE;
		}
		
		public function getUserFolders($userId) {
			return array();
		}
		
		function getItemDescription($item) {
			return NULL;
		}
				
		function setItemDescription($item, $description) {
			return FALSE;
		}
	
		function removeItemDescription($item) {
			return FALSE;
		}
		
		function moveItemDescription($from, $to) {
			return FALSE;
		}
					
		function getItemPermission($item, $userId) {
			return FALSE;
		}
	
		function getItemPermissions($item) {
			return FALSE;
		}
			
		function updateItemPermissions($updates) {
			return FALSE;
		}

		function removeItemPermissions($item) {
			return FALSE;
		}
		
		function moveItemPermissions($from, $to) {
			return FALSE;
		}
		
		function log() {
			Logging::logDebug("CONFIGURATION PROVIDER (".get_class($this)."): supported features=".Util::array2str($this->getSupportedFeatures())." auth=".$this->isAuthenticationRequired());
		}
	}
?>