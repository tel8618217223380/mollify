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

	class Session {
		private $name;
		
		public function __construct($settings) {
			$this->name = "MOLLIFY_SESSION";			
			if ($settings->setting("session_name") != NULL) $this->name .= "_".$settings->setting("session_name");
		}
		
		public function initialize($request) {
			session_name($this->name);
			if ($request->hasParam("MOLLIFY_SESSION_ID")) session_id($request->param("MOLLIFY_SESSION_ID"));
			session_start();
		}
		
		public function getSessionInfo() {
			$result = array();
			$result['session_name'] = session_name();
			$result['session_id'] = session_id();
			return $result;
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
				
		public function param($param, $value = NULL) {
			if ($value === NULL) return $_SESSION[$param];
			return $_SESSION[$param] = $value;
		}
		
		public function log() {
			Logging::logDebug("SESSION: ".Util::array2str($_SESSION));
		}

	}
?>