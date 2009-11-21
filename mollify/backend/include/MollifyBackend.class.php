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

	require_once("include/Logging.class.php");
	require_once("include/Settings.class.php");
	require_once("include/Session.class.php");
	require_once("include/Authentication.class.php");
	require_once("include/ServiceEnvironment.class.php");
	require_once("include/Util.class.php");
	require_once("include/filesystem/Filesystem.class.php");

	class MollifyBackend {
		private $environment;
		
		function __construct($settingsVar, $configurationProviderId, $responseHandler) {
			$settings = new Settings($settingsVar);
			$session = new Session($settings);
			$configurationProvider = $this->createConfigurationProvider($configurationProviderId);
			$authentication = new Authentication($session, $configurationProvider);
			
			$this->environment = new ServiceEnvironment($session, $authentication, $responseHandler, $configurationProvider, $settings);
			$this->setupEnvironment();
		}
	
		private function createConfigurationProvider($configurationProviderId) {
			require_once("configuration/ConfigurationProvider.class.php");
			if (!$configurationProviderId or strcasecmp($configurationProviderId, 'file') == 0) {
				require_once("configuration/FileConfigurationProvider.class.php");
				return new FileConfigurationProvider();
			} else {
				Logging::logError("Unsupported data provider: [".$configurationProviderId."]");
				throw new ServiceException("INVALID_CONFIGURATION", "Unsupported data provider: [".$configurationProviderId."]");
			}
		}

		private function setupEnvironment() {
			$this->environment->addService("authentication", "AuthenticationServices");
			$this->environment->addService("session", "SessionServices");
			$this->environment->addService("configuration", "ConfigurationServices");
			$this->environment->addService("file", "FilesystemServices");
		}
		
		public function processRequest($request) {
			$this->environment->initialize($request);
			
			$service = $this->environment->getService($request);
			if ($service->isAuthenticationRequired() and !$this->environment->getAuthentication()->isAuthenticated()) throw new ServiceException("UNAUTHORIZED");
			
			$service->processRequest();
		}
	}
?>