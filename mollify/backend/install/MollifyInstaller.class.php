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
	
	abstract class MollifyInstaller {
		private $type;
		private $settingsVar;

		private $settings;
		private $session;
		private $authentication;
		private $configuration;
		
		private $error = NULL;
		private $data = array();
		
		public function __construct($type, $settingsVar) {
			$this->type = $type;
			$this->settingsVar = $settingsVar;
			foreach($_POST as $key => $val) $this->data[$key] = $val;
			
			require_once("include/Logging.class.php");
			Logging::initialize($this->settingsVar);
			Logging::logDebug("Installer: ".get_class($this));
		}
		
		public function createEnvironment() {
			require_once("include/Settings.class.php");
			require_once("include/Session.class.php");
			require_once("include/Authentication.class.php");
			require_once("include/ConfigurationProviderFactory.class.php");
			$configurationProviderFactory = new ConfigurationProviderFactory();
			
			$this->settings = new Settings($this->settingsVar);
			$this->session = new Session($this->settings);
			$this->configuration = $configurationProviderFactory->createConfigurationProvider($this->type, $this->settings);
			$this->authentication = new Authentication($this);
			$this->session->initialize(NULL);
		}
		
		public abstract function isConfigured();
		
		public function onError($e) {
			Logging::logException($e);
		}
		
		public function session() {
			return $this->session;
		}
		
		public function authentication() {
			return $this->authentication;
		}
		
		public function configuration() {
			return $this->configuration;
		}
		
		public function hasError() {
			return $this->error != NULL;
		}
		
		public function error($err = NULL) {
			if ($err == NULL) return $this->error;
			$this->error = $err;
		}
	
		public function action() {
			return $this->data("action");
		}

		public function clearAction() {
			unset($this->data["action"]);
		}

		public function phase() {
			return $this->data("phase");
		}
				
		public function setPhase($val) {
			Logging::logDebug("New installer phase: [".$val."]");
			$this->data['phase'] = $val;
		}
		
		public function data($name = NULL) {
			if ($name == NULL) return $this->data;
			return isset($this->data[$name]) ? $this->data[$name] : NULL;
		}
		
		protected function showPage($page) {
			$page = $this->type."/"."page_".$page;
			Logging::logDebug("Opening page: ".$page." ".($this->error != NULL ? "(error=".$this->error.")" : ""));
			require($page.".php");
			die();
		}
	}
?>