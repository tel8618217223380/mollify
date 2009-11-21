<?php
	require_once("Features.class.php");
	
	class ServiceEnvironment {
		private $services = array();
		private $session;
		private $authentication; 
		private $responseHandler;
		private $configurationProvider;
		private $settings;
		private $filesystem;
		
		public function __construct($session, $authentication, $responseHandler, $configurationProvider, $settings) {
			$this->session = $session;
			$this->authentication = $authentication; 
			$this->responseHandler = $responseHandler;
			$this->configurationProvider = $configurationProvider;
			$this->settings = $settings;
			$this->features = new Features($configurationProvider, $settings);
			$this->filesystem = new Filesystem($settings);
		}
		
		public function getSession() {
			return $this->session;
		}

		public function getResponseHandler() {
			return $this->responseHandler;
		}
		
		public function getAuthentication() {
			return $this->authentication;
		}
		
		public function getConfigurationProvider() {
			return $this->configurationProvider;
		}

		public function getFeatures() {
			return $this->features;
		}

		public function getFilesystem() {
			return $this->filesystem;
		}
				
		public function initialize($request) {
			$this->session->initialize($request);
			$this->authentication->initialize($request);
			$this->filesystem->initialize($request);
			$this->log();
		}
						
		public function addService($path, $controller) {
			$this->services[$path] = $controller;
		}
		
//		public function startSession($userId = "", $username = "") {
//			$this->verifyConfiguration($userId);
//			
//			$this->authentication->initializeSession($userId, $username);
//			$this->configurationProvider->initializeSession($this->sessionHandler, $userId);
//		}
		
		private function verifyConfiguration($userId) {
			$roots = $this->configurationProvider->getUserRootDirectories($userId);
			
			foreach($roots as $id => $root) {
				if (!isset($root["name"])) {
					$this->session->reset();
					throw new ServiceException("INVALID_CONFIGURATION", "Root directory definition does not have a name (".$id.")");
				}
				
				if (!file_exists($root["path"])) {
					$this->session->reset();
					throw new ServiceException("INVALID_CONFIGURATION", "Root directory does not exist (".$id.")");
				}
			}
		}
		
		public function getService($request) {
			$parts = $request->getParts();
			$id = $parts[0];
			if (!array_key_exists($id, $this->services)) throw new ServiceException("Unknown service '".$id."'");
			$service = $this->createService($this->services[$id], $request, array_slice($parts, 1));
			if (Logging::isDebug()) $service->log();
			return $service;
		}
		
		private function createService($controller, $request, $path) {
			require_once("services/ServicesBase.class.php");
			require_once("services/".$controller.".class.php");
			return new $controller($this, $request, $path);
		}
		
		public function log() {
			if (!Logging::isDebug()) return;
			Logging::logSystem();
			
			$this->settings->log();
			$this->configurationProvider->log();
			$this->features->log();
			$this->filesystem->log();
			$this->session->log();
			$this->authentication->log();
		}
	}

	class ServiceException extends Exception {
		private $type;
		
		public function __construct($type, $details = "") {
			parent::__construct($details);
			$this->type = $type;
		}
		
		function getType() {
			return $this->type;
		}
		
		function getDetails() {
			return $this->getMessage();
		}
	}
?>