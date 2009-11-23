<?php
	class Authentication {
		public static $PERMISSION_VALUE_ADMIN = "A";
		public static $PERMISSION_VALUE_READWRITE = "RW";
		public static $PERMISSION_VALUE_READONLY = "RO";
	
		private $env;
		
		public function __construct($env) {
			$this->env = $env;
		}
		
		public function initialize($request) {
			if (!$this->env->authentication()->isAuthenticationRequired() and !$this->env->authentication()->isAuthenticated()) $this->authenticate("", "");
		}
		
		public function authenticate($userId, $password) {
			$user = $this->env->configuration()->findUser($userId, $password);
			if (!$user)
				throw new ServiceException("AUTHENTICATION_FAILED");
			
			$this->env->session()->param('user_id', $user["id"]);
			$this->env->session()->param('username', $user["name"]);
			$this->env->session()->param('default_permission', $this->env->configuration()->getDefaultPermissionMode($user["id"]));
			
			$this->env->onSessionStarted();
		}
		
		public function isAuthenticated() {
			return $this->env->session()->hasParam('user_id');
		}

		public function isAuthenticationRequired() {
			return $this->env->configuration()->isAuthenticationRequired();
		}

		public function getUserId() {
			return $this->env->session()->param('user_id');
		}
		
		public function getUserInfo() {
			return array(
				'user_id' => $this->env->session()->param('user_id'),
				'username' => $this->env->session()->param('username'),
				'default_permission' => $this->env->session()->param('default_permission')
			);
		}
		
		public function getDefaultPermission() {
			return $this->session->param('default_permission');
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