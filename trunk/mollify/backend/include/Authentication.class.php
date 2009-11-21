<?php
	class Authentication {
		public static $PERMISSION_VALUE_ADMIN = "A";
		public static $PERMISSION_VALUE_READWRITE = "RW";
		public static $PERMISSION_VALUE_READONLY = "RO";
	
		private $session;
		private $configurationProvider;
		
		public function __construct($session, $configurationProvider) {
			$this->session = $session;
			$this->configurationProvider = $configurationProvider;
		}
		
		public function initialize($request) {
			if (!$this->isAuthenticationRequired() and !$this->isAuthenticated()) $this->authenticate("", "");
		}
		
		public function authenticate($userId, $username) {
			$this->session->setSessionParam('user_id', $userId);
			$this->session->setSessionParam('username', $username);
			$this->session->setSessionParam('default_permission', $this->configurationProvider->getDefaultPermissionMode($userId));
		}
		
		public function isAuthenticated() {
			return $this->session->hasSessionParam('user_id');
		}

		public function isAuthenticationRequired() {
			return $this->configurationProvider->isAuthenticationRequired();
		}
		
		public function getSessionInfo() {
			$result = array();
			$result['user_id'] = $this->session->getSessionParam('user_id');
			$result['username'] = $this->session->getSessionParam('username');
			$result['default_permission'] = $this->session->getSessionParam('default_permission');
			return $result;
		}
		
		public function getDefaultPermission() {
			return $this->session->getSessionParam('default_permission');
		}
		
		function hasModifyRights() {
			$base = $this->getDefaultPermission();
			return ($base === self::$PERMISSION_VALUE_ADMIN || $base === self::$PERMISSION_VALUE_READWRITE);
		}
		
		function isAdmin() {
			return ($this->getDefaultPermission() === self::$PERMISSION_VALUE_ADMIN);
		}
		
		public function log() {
			Logging::logDebug("AUTH: is_authenticated_required=".$this->isAuthenticationRequired().", is_authenticated=".$this->isAuthenticated());
		}
	}
?>