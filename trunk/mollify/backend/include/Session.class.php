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
	
	class Session {
		const EVENT_TYPE_SESSION = "session";
		
		protected $name;
		protected $env;
		
		public function __construct($settings) {
			$this->name = "MOLLIFY-SESSION";			
			if ($settings->hasSetting("session_name")) {
				$n = $settings->setting("session_name");
				if (strlen($n) > 0) $this->name .= "-".$n;
			}
		}
		
		public function initialize($env, $id = NULL) {
			$this->env = $env;
			if ($env != NULL and $env->events() != NULL) {
				require_once("include/event/SessionEvent.class.php");
				SessionEvent::register($env->events());
				if (!$env->configuration()->isAuthenticationRequired()) return;
			}
			
			session_name($this->name);
			if ($id) session_id($id);
			session_start();
		}
		
		public function isActive() {
			return $this->env->configuration()->isAuthenticationRequired();
		}
		
		public function getSessionInfo() {
			$result = array();
			if ($this->env->configuration()->isAuthenticationRequired()) {
				$result['session_name'] = session_name();
				$result['session_id'] = session_id();
			}
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
			if ($this->env == NULL) throw new ServiceException("env null");
			if (!$this->env->configuration()->isAuthenticationRequired()) return FALSE;
			return isset($_SESSION[$param]);
		}

		public function removeParam($param) {
			if (!$this->env->configuration()->isAuthenticationRequired()) return;
			unset($_SESSION[$param]);
		}
				
		public function param($param, $value = NULL) {
			if ($this->env != NULL and !$this->env->configuration()->isAuthenticationRequired()) return NULL;
			
			if ($value === NULL) {
				if (!array_key_exists($param, $_SESSION)) throw new ServiceException("Invalid session param requested: ".$param);
				return $_SESSION[$param];
			}
			return $_SESSION[$param] = $value;
		}
		
		public function all() {
			if (!$this->env->configuration()->isAuthenticationRequired()) return array();
			return $_SESSION;
		}
		
		public function log() {
			Logging::logDebug("SESSION: ".Util::array2str($this->all(), array("debug_info")));
		}

		public function __toString() {
			return "Session";
		}
	}	
?>