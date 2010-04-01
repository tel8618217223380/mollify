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
	require_once("include/event/EventHandler.class.php");

	class Session {
		const EVENT_TYPE_SESSION = "session";
		
		protected $name;
		
		public function __construct($settings) {
			$this->name = "MOLLIFY-SESSION";			
			if ($settings->hasSetting("session_name")) {
				$n = $settings->setting("session_name");
				if (strlen($n) > 0) $this->name .= "-".$n;
			}
		}
		
		public function initialize($request, $env) {
			if ($env != NULL) SessionEvent::register($env->events());
			
			session_name($this->name);
			if ($request != NULL and $request->hasParam("session")) session_id($request->param("session"));
			session_start();
		}
		
		public function getSessionInfo() {
			$result = array();
			$result['session_name'] = session_name();
			$result['session_id'] = session_id();
			$result['session_ver'] = "1_5";
			return $result;
		}
		
		public function getSessionVer() {
			if (!$this->hasParam('session_ver')) return NULL;
			return $this->param('session_ver');
		}
		
		public function reset() {
			$_SESSION = array();
			if (isset($_COOKIE[session_name()]))
				setcookie(session_name(), '', time()-42000, '/');
			session_destroy();
		}

		public function hasParam($param) {
			return isset($_SESSION[$param]);
		}

		public function removeParam($param) {
			unset($_SESSION[$param]);
		}
				
		public function param($param, $value = NULL) {
			if ($value === NULL) return $_SESSION[$param];
			return $_SESSION[$param] = $value;
		}
		
		public function all() {
			return $_SESSION;
		}
		
		public function log() {
			Logging::logDebug("SESSION: ".Util::array2str($_SESSION));
		}

		public function __toString() {
			return "Session";
		}
	}
	
	 class SessionEvent extends Event {
		const LOGIN = "login";
		const LOGOUT = "logout";
		const FAILED_LOGIN = "failed_login";
		
		private $info;
		
		static function register($eventHandler) {
			$eventHandler->registerEventType(Session::EVENT_TYPE_SESSION, self::LOGIN, "Login");
			$eventHandler->registerEventType(Session::EVENT_TYPE_SESSION, self::LOGOUT, "Logout");
			$eventHandler->registerEventType(Session::EVENT_TYPE_SESSION, self::FAILED_LOGIN, "Failed login");
		}
		
		static function login($ip) {
			return new SessionEvent(self::LOGIN, "ip=".$ip);
		}

		static function logout($ip) {
			return new SessionEvent(self::LOGOUT, "ip=".$ip);
		}

		static function failedLogin($userId, $ip) {
			return new SessionEvent(self::FAILED_LOGIN, 'user='.$userId.";ip=".$ip);
		}
		
		function __construct($type, $info = '') {
			parent::__construct(time(), Session::EVENT_TYPE_SESSION, $type);
			$this->info = $info;
		}

		public function itemToStr() {
			return '';
		}
				
		public function details() {
			return $this->info;
		}
	}
?>