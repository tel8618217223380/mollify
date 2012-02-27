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


	require_once("include/event/EventHandler.class.php");
	require_once("include/session/Session.class.php");
	require_once("include/ServiceEnvironment.class.php");
	require_once("include/Util.class.php");
	require_once("include/configuration/UserEvent.class.php");

	class MollifyBackend {
		private $environment;
		
		function __construct($settings, $configuration, $responseHandler) {
			$this->settings = $settings;
			$this->session = new Session($settings);
			$this->configuration = $configuration;
			$this->environment = new ServiceEnvironment($this->session, $responseHandler, $this->configuration, $settings);
			$this->setup();
		}
	
		private function setup() {
			$this->environment->addService("authentication", "AuthenticationServices");
			$this->environment->addService("session", "SessionServices");
			$this->environment->addService("configuration", "ConfigurationServices");
			$this->environment->addService("filesystem", "FilesystemServices");
			$this->environment->addService("events", "EventServices");
			/*if (Logging::isDebug()) {
				$this->environment->addService("debug", "DebugServices");
				$this->environment->response()->addListener($this);
			}*/
			
			UserEvent::register($this->environment->events());
			
			$this->environment->plugins()->setup();
		}
		
		public function onResponseSent() {
			//if (!$this->environment->request()) return;
			//$path = $this->environment->request()->path();
			/*if (count($path) > 0 and (strcasecmp($path[0], "debug") == 0)) return;
			
			if (!$this->environment->session()->hasParam("debug_info"))
				$debug = array();
			else
				$debug = $this->environment->session()->param("debug_info");
			
			$debug[] = Logging::getTrace();
			while (count($debug) > 5) {
				unset($debug[0]);
				$debug = array_values($debug);
			}
			$this->environment->session()->param("debug_info", $debug);*/
		}
		
		public function env() {
			return $this->environment;
		}
		
		public function processRequest($request) {
			$this->environment->initialize($request);
			$service = $this->environment->getService($request);

			if (!$service->isAuthenticated()) {
				$this->environment->session()->end();
				throw new ServiceException("UNAUTHORIZED");
			}
			
			$service->processRequest();
		}
		
		public function __toString() {
			return "MollifyBackend";
		}
	}
?>