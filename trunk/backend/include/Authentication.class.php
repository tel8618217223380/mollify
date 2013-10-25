<?php

	/**
	 * Authentication.class.php
	 *
	 * Copyright 2008- Samuli Järvelä
	 * Released under GPL License.
	 *
	 * License: http://www.mollify.org/license.php
	 */

	class Authentication {
		const PERMISSION_VALUE_ADMIN = "A";
		const PERMISSION_VALUE_READWRITE = "RW";
		const PERMISSION_VALUE_READWRITE_NODELETE = "WD";
		const PERMISSION_VALUE_READONLY = "RO";
		const PERMISSION_VALUE_NO_RIGHTS = 'NO';
		
		const RIGHTS_NONE = "NO";
		const RIGHTS_READ = "R";
		const RIGHTS_WRITE = "W";
		const RIGHTS_DELETE = "D";
		const RIGHTS_ADMIN = "A";
	
		protected $env;
		
		private $cachedDefaultPermission = FALSE;
		
		public function __construct($env) {
			$this->env = $env;
		}
		
		public function initialize() {
		}
		
		public function assertPermissionValue($value) {
			if ($value != self::PERMISSION_VALUE_ADMIN and $value != self::PERMISSION_VALUE_READWRITE and $value != self::PERMISSION_VALUE_READWRITE_NODELETE and $value != self::PERMISSION_VALUE_READONLY and $value != self::PERMISSION_VALUE_NO_RIGHTS)
				throw new ServiceException("INVALID_CONFIGURATION", "Invalid permission mode [".$value."]");
		}
		
		public function check() {
			if ($this->isAuthenticated()) {
				if (!$this->env->session()->hasParam('auth') or strcasecmp("remote", $this->env->session()->param('auth')) != 0) return;
				if (isset($_SERVER["REMOTE_USER"]) and strcasecmp($this->env->session()->username(), $_SERVER["REMOTE_USER"]) == 0) return;

				// remote user has changed, end session
				$this->env->session()->end();
			}
			
			Logging::logDebug("No authenticated session active");
			$methods = $this->env->settings()->setting("authentication_methods");
			if (in_array("remote", $methods) and $this->checkRemoteAuth()) return;
			if ($this->checkStoredCookieAuth()) return;
		}
		
		private function checkRemoteAuth() {
			if (!isset($_SERVER["REMOTE_USER"])) return FALSE;
			
			$userName = $_SERVER["REMOTE_USER"];
			Logging::logDebug("Remote authentication found for [".$userName."] ".(isset($_SERVER["AUTH_TYPE"]) ? $_SERVER["AUTH_TYPE"] : ""));
			
			$user = $this->env->configuration()->getUserByName($userName);
			if ($user == NULL) return FALSE;
			
			Logging::logDebug("Remote authentication succeeded for [".$user["id"]."] ".$user["name"]);
			$this->doAuth($user, "remote");
			return true;
		}
		
		private function checkStoredCookieAuth() {
			if (!$this->env->cookies()->exists("login")) return FALSE;
			$data = $this->env->cookies()->get("login");
			Logging::logDebug("Stored login data ".$data);
			if (!$data or strlen($data) == 0) return FALSE;
			
			$parts = explode(":", $data);
			if (count($parts) != 2) {
				Logging::logDebug("Invalid auth cookie string: ".$data);
				return FALSE;
			}
			$userId = $parts[0];
			$token = $parts[1];
			
			$user = $this->env->configuration()->getUser($userId, time());
			if ($user == NULL) return FALSE;
			
			$check = $this->getCookieAuthString($user);
			if (strcmp($token, $check) != 0) {
				Logging::logDebug("Login cookie found for user ".$userId.", but auth key did not match");
				return FALSE;
			}
			Logging::logDebug("Stored authentication succeeded for user [".$user["id"]."] ".$user["name"]);
			$this->doAuth($user, "cookie");
		}
		
		private function getCookieAuthString($user) {
			return md5($user["name"]."/".$user["password"]);
		}
		
		public function storeCookie() {
			$user = $this->env->session()->user();
			$data = $user["id"].":".$this->getCookieAuthString($user);
			$this->env->cookies()->add("login", $data, time()+60*60*24*30);
		}
		
		public function logout() {
			if (!$this->env->cookies()->exists("login")) return;
			$this->env->cookies()->remove("login");
		}
		
		public function authenticate($userId, $pw) {
			$user = $this->env->configuration()->findUser($userId, $this->env->settings()->setting("email_login"), time());
			if (!$user) {
				syslog(LOG_NOTICE, "Failed Mollify login attempt from [".$this->env->request()->ip()."], user [".$userId."]");
				$this->env->events()->onEvent(SessionEvent::failedLogin($userId, $this->env->request()->ip()));
				throw new ServiceException("AUTHENTICATION_FAILED");
			}
			
			$auth = $this->env->configuration()->getUserAuth($user["id"]);
			if (!$auth) throw new ServiceException("INVALID_CONFIGURATION", "User auth info missing ".$userId);
			
			$authType = $auth["type"];
			if ($authType == NULL) $authType = $this->getDefaultAuthenticationMethod();
			$authModule = $this->getAuthenticationModule($authType);
			if (!$authModule) throw new ServiceException("INVALID_CONFIGURATION", "Invalid auth module: ".$auth);
			
			$authModule->authenticate($user, $pw, $auth);
			$this->doAuth($user, $authType);
			return $user;
		}
		
		private function getAuthenticationModule($id) {
			$setting = "auth_module_".strtolower($id);
			if ($this->env->settings()->hasSetting($setting))
				$cls = $this->env->settings()->setting($setting);
			else
				$cls = "include/auth/Authenticator".strtoupper($id).".class.php";
			require_once($cls);
			$name = "Mollify_Authenticator_".strtoupper($id);
			return new $name($this->env);
		}
		
		public function getDefaultAuthenticationMethod() {
			$m = $this->env->settings()->setting("authentication_methods");
			return $m[0];
		}
		
		public function doAuth($user, $authType = NULL) {
			$this->env->session()->start($user, array("auth" => $authType));
		}
		
		public function realm() {
			return "mollify";
		}
		
		public function isAuthenticated() {
			return $this->env->session()->isActive();
		}
		
		public function getDefaultPermission() {
			if (!$this->cachedDefaultPermission) {
				if (!$this->isAuthenticated()) $this->cachedDefaultPermission = self::PERMISSION_VALUE_NO_RIGHTS;
				else $this->cachedDefaultPermission = $this->env->configuration()->getDefaultPermission($this->env->session()->userId());
			}
			return $this->cachedDefaultPermission;
		}
		
		public function assertRights($permissions, $required, $desc = "Unknown item/action") {
			if ($this->isAdmin() or strcasecmp($required, self::RIGHTS_NONE) === 0) return;

			if (strcasecmp($permissions, self::PERMISSION_VALUE_READWRITE_NODELETE) === 0) {
				if ($required === self::RIGHTS_READ or $required === self::RIGHTS_WRITE) return;
			}					
			if (strcasecmp($permissions, self::PERMISSION_VALUE_READWRITE) === 0) {
				if ($required === self::RIGHTS_READ or $required === self::RIGHTS_WRITE or $required === self::RIGHTS_DELETE) return;
			}
			if (strcasecmp($permissions, self::PERMISSION_VALUE_READONLY) === 0) {
				if ($required === self::RIGHTS_READ) return;
			}
			
			throw new ServiceException("INSUFFICIENT_RIGHTS", $desc.", required:".$required.", permissions:".$permissions);
		}
		
		public function hasReadRights($permission) {
			return strcasecmp($permission, self::PERMISSION_VALUE_ADMIN) === 0 or strcasecmp($permission, self::PERMISSION_VALUE_READWRITE) === 0 or strcasecmp($permission, self::PERMISSION_VALUE_READWRITE_NODELETE) === 0 or strcasecmp($permission, self::PERMISSION_VALUE_READONLY) === 0;
		}
		
		function hasModifyRights() {
			$base = $this->getDefaultPermission();
			return ($base === self::PERMISSION_VALUE_ADMIN || $base === self::PERMISSION_VALUE_READWRITE || $base === self::PERMISSION_VALUE_READWRITE_NODELETE);
		}

		function hasDeleteRights() {
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
			Logging::logDebug("AUTH: is_authenticated=".$this->isAuthenticated());
		}
		
		public function __toString() {
			return "Authentication";
		}
	}
	
	abstract class Mollify_Authenticator {
		abstract function authenticate($user, $pw, $auth);
	}
?>
