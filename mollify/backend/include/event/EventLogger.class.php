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

	class EventLogger {
		private $env;
		
		public function __construct($env) {
			$this->env = $env;
		}
		
		public function onEvent($e) {
			//TODO configuration
			if ($e->typeId() != 'filesystem/download') return;
			
			$time = date('YmdHis', $e->time());
			$user = $this->env->authentication()->getUserInfo();
			$item = $e->itemToStr();
			$description = $e->description();
			$type = $e->typeId();
			
			$db = $this->env->configuration()->db();
			$db->update(sprintf("INSERT INTO ".$db->table("event_log")." (time, user, type, item, description) VALUES (%s, '%s', '%s', '%s', '%s')", $time, $db->string($user['username']), $db->string($type), $db->string($item), $db->string($description)));
		}
		
		public function __toString() {
			return "EventHandler";
		}
	}
?>