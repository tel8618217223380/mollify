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

	class Authentication {
		const PERMISSION_VALUE_ADMIN = "A";
		const PERMISSION_VALUE_READWRITE = "RW";
		const PERMISSION_VALUE_READONLY = "RO";
		const PERMISSION_VALUE_NO_RIGHTS = 'NO';
		
		const RIGHTS_NONE = "NO";
		const RIGHTS_READ = "R";
		const RIGHTS_WRITE = "W";
		const RIGHTS_ADMIN = "A";
	
		protected $env;
		
		public function __construct($env) {
			$this->env = $env;
		}
		
		public function initialize() {}
		
		public function assertPermissionValue($value) {
			if ($value != self::PERMISSION_VALUE_ADMIN and $value != self::PERMISSION_VALUE_READWRITE and $value != self::PERMISSION_VALUE_READONLY and $value != self::PERMISSION_VALUE_NO_RIGHTS)
				throw new ServiceException("INVALID_CONFIGURATION", "Invalid permission mode [".$value."]");
		}
		
		public function check() {
			if (!$this->isAuthenticationRequired()) return;
			if ($this->isAuthenticated()) {
				if ($this->env->session()->hasParam('auth') and strcasecmp("remote", $this->env->session()->param('auth')) != 0) return;
				if (strcasecmp($this->env->session()->param('username'), $_SERVER["REMOTE_USER"]) == 0) return;

				// remote user has changed, reset old session
				$this->env->session()->removeParam('user_id');
				$this->env->session()->removeParam('username');
				$this->env->session()->removeParam('default_permission');
			}
			
			$methods = $this->env->settings()->setting("authentication_methods",TRUE);
			if (!in_array("remote", $methods)) return;
			
			Logging::logDebug("No authenticated session active, attempting remote authentication");
			if (!isset($_SERVER["REMOTE_USER"])) return;
			
			$userName = $_SERVER["REMOTE_USER"];
			Logging::logDebug("Remote authentication found for [".$userName."] ".(isset($_SERVER["AUTH_TYPE"]) ? $_SERVER["AUTH_TYPE"] : ""));
			
			$user = $this->env->configuration()->getUserByName($userName);
			if ($user == NULL) return;
			
			Logging::logDebug("Remote authentication succeeded for [".$user["id"]."] ".$user["name"]);
			$this->doAuth($user, "remote");
		}
		
		public function authenticate($userId, $pw) {
			$password = md5($pw);
			
			$user = $this->env->configuration()->findUser($userId, $password, $this->env->settings("email_login", TRUE));
			if (!$user) {
				syslog(LOG_NOTICE, "Failed Mollify login attempt from [".$this->env->request()->ip()."], user [".$userId."]");
				$this->env->events()->onEvent(SessionEvent::failedLogin($userId, $this->env->request()->ip()));
				throw new ServiceException("AUTHENTICATION_FAILED");
			}
			
			$auth = $user["auth"];
			if ($auth == NULL) $auth = $this->getDefaultAuthenticationMethod();
			
			if (strcasecmp("PW", $auth) != 0) {
				// handle other authentications
				if (strcasecmp("LDAP", $auth) == 0) {
					$this->authenticateLDAP($user, $pw);
					$this->env->session()->param('auth', "ldap");
					return;
				} 
				throw new ServiceException("INVALID_CONFIGURATION", "Unsupported authentication type ".$user["auth"]);
			}
			$this->doAuth($user, $auth);
		}
		
		public function getDefaultAuthenticationMethod() {
			$m = $this->env->settings()->setting("authentication_methods",TRUE);
			return $m[0];
		}
		
		private function authenticateLDAP($user, $pw) {
			$conn = @ldap_connect($this->env->settings()->setting("ldap_server"));
			if (!$conn)
				throw new ServiceException("INVALID_CONFIGURATION", "Could not connect to LDAP server");
	
			$bind = @ldap_bind($conn, $user["name"]."@".$this->env->settings()->setting("ldap_fqdn"), $pw);
			if (!$bind) {
				Logging::logDebug("LDAP error: ".ldap_error($conn));
				throw new ServiceException("AUTHENTICATION_FAILED");
			}
			ldap_close($conn);
		}

		public function doAuth($user, $auth = NULL) {
			$this->env->session()->param('user_id', $user["id"]);
			if ($this->env->features()->isFeatureEnabled('user_groups'))
				$this->env->session()->param('groups', $this->env->configuration()->getUsersGroups($user["id"]));
			$this->env->session()->param('username', $user["name"]);
			$this->env->session()->param('default_permission', $this->env->configuration()->getDefaultPermission($user["id"]));
			if ($auth != NULL) $this->env->session()->param('auth', $auth);
		}
		
		public function realm() {
			return "mollify";
		}
		
		public function isAuthenticated() {
			return $this->env->session()->hasParam('user_id');
		}

		public function isAuthenticationRequired() {
			return $this->env->configuration()->isAuthenticationRequired();
		}

		public function getUserId() {
			if (!$this->isAuthenticated()) return NULL;
			return $this->env->session()->param('user_id');
		}

		public function getUsername() {
			if (!$this->isAuthenticated()) return NULL;
			return $this->env->session()->param('username');
		}
		
		public function getUserGroups() {
			return $this->env->session()->param('groups');
		}
		
		public function hasUserGroups() {
			return $this->env->session()->hasParam("groups");
		}
		
		public function getUserInfo() {
			return array(
				'user_id' => $this->getUserId(),
				'username' => $this->getUsername(),
				'default_permission' => $this->getDefaultPermission()
			);
		}
		
		public function getDefaultPermission() {
			if (!$this->isAuthenticated()) return $this->env->configuration()->getDefaultPermission();
			return $this->env->session()->param('default_permission');
		}
		
		public function assertRights($permissions, $required, $desc = "Unknown item/action") {
			if ($this->isAdmin() or strcasecmp($required, self::RIGHTS_NONE) === 0) return;
					
			if (strcasecmp($permissions, self::PERMISSION_VALUE_READWRITE) === 0) {
				if ($required === self::RIGHTS_READ or $required === self::RIGHTS_WRITE) return;
			}
			if (strcasecmp($permissions, self::PERMISSION_VALUE_READONLY) === 0) {
				if ($required === self::RIGHTS_READ) return;
			}
			
			throw new ServiceException("INSUFFICIENT_RIGHTS", $desc.", required:".$required.", permissions:".$permissions);
		}
		
		public function hasReadRights($permission) {
			return strcasecmp($permission, self::PERMISSION_VALUE_ADMIN) === 0 or strcasecmp($permission, self::PERMISSION_VALUE_READWRITE) === 0 or strcasecmp($permission, self::PERMISSION_VALUE_READONLY) === 0;
		}
		
		function hasModifyRights() {
			$base = $this->getDefaultPermission();
			return ($base === self::PERMISSION_VALUE_ADMIN || $base === self::PERMISSION_VALUE_READWRITE);
		}

		function assertAdmin() {
			if (!$this->isAdmin()) throw new ServiceException("NOT_AN_ADMIN");
		}
		
		function isAdmin() {
			return ($this->isAuthenticated() and ($this->getDefaultPermission() === self::PERMISSION_VALUE_ADMIN));
		}
		
		public function log() {
			Logging::logDebug("AUTH: is_authentication_required=".$this->isAuthenticationRequired().", is_authenticated=".$this->isAuthenticated());
		}
		
		public function __toString() {
			return "Authentication";
		}
	}
?>