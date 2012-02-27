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
		
		protected $id = NULL;
		protected $env;
		protected $dao;
		protected $session = array();
		protected $data = array();
		protected $user = NULL;
		protected $userGroups = NULL;
		
		public function __construct($settings) {}
		
		public function initialize($env, $rq) {
			$this->env = $env;
			require_once("SessionDao.class.php");
			$this->dao = new SessionDao($env);
						
			if ($env != NULL and $env->events() != NULL) {
				require_once("include/event/SessionEvent.class.php");
				SessionEvent::register($env->events());
				if (!$env->configuration()->isAuthenticationRequired()) return;
			}
			
			$id = NULL;
			if ($rq != NULL) $id = $rq->getSessionId();
			if ($id == NULL and $this->env->cookies()->exists("session"))
				$id = $this->env->cookies()->get("session");

			if ($id != NULL) {
				$time = time();
				$expiration = $this->getLastValidSessionTime($time);
				
				$sessionData = $this->dao->getSession($id, $this->env->configuration()->formatTimestampInternal($expiration));
				if ($sessionData == NULL) {
					$this->env->cookies()->remove("session");
					throw new ServiceException("INVALID_REQUEST", "Invalid session");
				}
				$this->id = $id;
				$this->session = $sessionData;
				$this->data = $this->dao->getSessionData($id);
				
				// load user data
				$this->user = $this->env->configuration()->getUser($sessionData["user_id"]);
				if ($this->env->features()->isFeatureEnabled('user_groups'))
					$this->userGroups = $this->env->configuration()->getUsersGroups($this->user["id"]);
			
				// extend session time
				$this->dao->updateSessionTime($this->id, $this->env->configuration()->formatTimestampInternal($time));
				$this->env->cookies()->add("session", $id, $this->getSessionExpirationTime($time));
			}
		}
		
		public function user() {
			return $this->user;
		}

		public function username() {
			return $this->user["name"];
		}
		
		public function userId() {
			if (!$this->isActive()) return NULL;
			return $this->user["id"];
		}
		
		public function hasUserGroups() {
			if (!$this->isActive()) return FALSE;
			return $this->userGroups != NULL and count($this->userGroups) > 0;
		}
		
		public function userGroups() {
			if (!$this->isActive()) return NULL;
			return $this->userGroups;
		}
				
		private function getSessionExpirationTime($from = NULL) {
			$added = 60*60;
			if (!$from) return time() + $added;
			return $from + $added;
		}

		private function getLastValidSessionTime($from = NULL) {
			$removed = 60*60;
			if (!$from) return time() - $removed;
			return $from - $removed;
		}
				
		public function start($user, $data) {
			$this->id = uniqid(TRUE);
			$this->user = $user;
			if ($this->env->features()->isFeatureEnabled('user_groups'))
				$this->userGroups = $this->env->configuration()->getUsersGroups($this->user["id"]);
			$this->data = $data;
			
			$time = time();
			$this->dao->addSession($this->id, $this->user["id"], $this->env->request()->ip(), $this->env->configuration()->formatTimestampInternal($time));
			if ($data and count($data) > 0) $this->dao->addSessionData($this->id, $data);
			$this->env->cookies()->add("session", $this->id, $this->getSessionExpirationTime($time));
		}
		
		public function isActive() {
			return $this->id != NULL;
		}
		
		public function getSessionInfo() {
			$result = array();
			$result['session_id'] = $this->id;
			$result['session_ver'] = "1_8_7";
			if ($this->isActive()) {
				$result['user_id'] = $this->userId();
				$result['username'] = $this->username();
			}
			return $result;
		}
		
		public function end() {
			if ($this->isActive()) {
				$this->dao->removeSession($this->id);
			}
			$this->env->cookies()->remove("session");
		}

		public function hasParam($param) {
			if (!$this->isActive()) return FALSE;
			return array_key_exists($param, $this->data);
		}
				
		public function param($param, $value = NULL) {
			if ($value === NULL) return $this->data[$param];
			$this->data[$param] = $value;
			$this->dao->addOrSetSessionData($this->id, $param, $value);
		}
		
		public function log() {}

		public function __toString() {
			return "Session";
		}
	}	
?>