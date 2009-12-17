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

	require_once("include/Settings.class.php");
	require_once("include/Session.class.php");
	require_once("include/Authentication.class.php");
	require_once("include/ServiceEnvironment.class.php");
	require_once("include/Util.class.php");

	class MollifyBackend {
		private $environment;
		
		function __construct($settingsVar, $configurationProviderId, $responseHandler) {
			$settings = new Settings($settingsVar);
			$session = new Session($settings);
			$configurationProvider = $this->createConfigurationProvider($configurationProviderId, $settings);
			
			$this->environment = new ServiceEnvironment($session, $responseHandler, $configurationProvider, $settings);
			$this->setup();
		}
	
		private function createConfigurationProvider($configurationProviderId, $settings) {
			require_once("configuration/ConfigurationProvider.class.php");
			if (!$configurationProviderId or strcasecmp($configurationProviderId, 'file') == 0) {
				require_once("configuration/FileConfigurationProvider.class.php");
				return new FileConfigurationProvider($settings);
			} else if (!$configurationProviderId or strcasecmp($configurationProviderId, 'mysql') == 0) {
				require_once("configuration/MySQLConfigurationProvider.class.php");
				return new MySQLConfigurationProvider($settings);
			} else {
				Logging::logError("Unsupported data provider: [".$configurationProviderId."]");
				throw new ServiceException("INVALID_CONFIGURATION", "Unsupported data provider: [".$configurationProviderId."]");
			}
		}

		private function setup() {
			$this->environment->addService("authentication", "AuthenticationServices");
			$this->environment->addService("session", "SessionServices");
			$this->environment->addService("configuration", "ConfigurationServices");
			$this->environment->addService("filesystem", "FilesystemServices");
		}
		
		public function processRequest($request) {
			$this->environment->initialize($request);
			$service = $this->environment->getService($request);
			
			if ($service->isAuthenticationRequired() and !$this->environment->authentication()->isAuthenticated()) {
				$this->environment->session()->reset();
				throw new ServiceException("UNAUTHORIZED");
			}
			
			$service->processRequest();
		}
	}
?>