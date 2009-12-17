<?php

	/**
	 * Copyright (c) 2008- Samuli JŠrvelŠ
	 *
	 * All rights reserved. This program and the accompanying materials
	 * are made available under the terms of the Eclipse Public License v1.0
	 * which accompanies this distribution, and is available at
	 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
	 * this entire header must remain intact.
	 */

	class Authentication {
		public static $PERMISSION_VALUE_ADMIN = "A";
		public static $PERMISSION_VALUE_READWRITE = "RW";
		public static $PERMISSION_VALUE_READONLY = "RO";
		
		const RIGHTS_NONE = "-";
		const RIGHTS_READ = "R";
		const RIGHTS_WRITE = "W";
		const RIGHTS_ADMIN = "A";
	
		private $env;
		
		public function __construct($env) {
			$this->env = $env;
		}
		
		public function initialize($request) {
			if (!$this->env->authentication()->isAuthenticationRequired() and !$this->env->authentication()->isAuthenticated()) $this->authenticate("", "");
		}
		
		public function assertPermissionValue($value) {
			if ($value != self::$PERMISSION_VALUE_ADMIN and $value != self::$PERMISSION_VALUE_READWRITE and $value != self::$PERMISSION_VALUE_READONLY)
				throw new ServiceException("INVALID_CONFIGURATION", "Invalid permission mode [".$value."]");
		}
		
		public function authenticate($userId, $password) {
			$user = $this->env->configuration()->findUser($userId, $password);
			if (!$user)
				throw new ServiceException("AUTHENTICATION_FAILED");
			
			$this->env->session()->param('user_id', $user["id"]);
			$this->env->session()->param('username', $user["name"]);
			$this->env->session()->param('default_permission', $this->env->configuration()->getDefaultPermission($user["id"]));
			
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
			return $this->env->session()->param('default_permission');
		}
		
		public function assertRights($permissions, $required, $desc = "Unknown item/action") {
			if ($required === self::RIGHTS_NONE or $this->isAdmin()) return;
					
			if ($permissions === self::$PERMISSION_VALUE_READWRITE) {
				if ($required === self::RIGHTS_READ or $required === self::RIGHTS_WRITE) return;
			}
			if ($permissions === self::$PERMISSION_VALUE_READ) {
				if ($required === self::RIGHTS_READ) return;
			}
			
			throw new ServiceException("INSUFFICIENT_RIGHTS", $desc.", required:".$required.", permissions:".$permissions);
		}
		
		function hasModifyRights() {
			$base = $this->getDefaultPermission();
			return ($base === self::$PERMISSION_VALUE_ADMIN || $base === self::$PERMISSION_VALUE_READWRITE);
		}

		function assertAdmin() {
			if (!$this->isAdmin()) throw new ServiceException("NOT_AN_ADMIN");
		}
		
		function isAdmin() {
			return ($this->getDefaultPermission() === self::$PERMISSION_VALUE_ADMIN);
		}
		
		public function log() {
			Logging::logDebug("AUTH: is_authentication_required=".$this->isAuthenticationRequired().", is_authenticated=".$this->isAuthenticated());
		}
	}
?>