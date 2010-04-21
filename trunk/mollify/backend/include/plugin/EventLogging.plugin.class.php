<?php

	/**
	 * Copyright (c) 2008- Samuli J�rvel�
	 *
	 * All rights reserved. This program and the accompanying materials
	 * are made available under the terms of the Eclipse Public License v1.0
	 * which accompanies this distribution, and is available at
	 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
	 * this entire header must remain intact.
	 */

	require_once("event_logging/EventLogger.class.php");
	
	class EventLogging extends PluginBase {
		const ID = "event_logging";
		
		public function __construct($env) {
			parent::__construct($env, self::ID);
		}
		
		public function setup() {
			if (!$this->env->features()->isFeatureEnabled('event_logging'))
				return;
			
			$logged = $this->env->settings()->setting("logged_events", TRUE);
			if (!$logged or count($logged) == 0) $logged = array("*");
			
			$this->addService("events", "EventServices");
			$e = new EventLogger($this->env);
			
			foreach($logged as $l)
				$this->env->events()->register($l, $e);
		}
		
		public function __toString() {
			return "EventLoggingPlugin";
		}
	}
?>