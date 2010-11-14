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