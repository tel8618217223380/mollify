<?php
	class ServicesBase {
		protected $env;
		protected $request;
		protected $path;
		
		public function __construct($serviceEnvironment, $request, $path) {
			$this->env = $serviceEnvironment;
			$this->request = $request;
			$this->path = $path;
		}
		
		public function isAuthenticationRequired() {
			return $this->env->getConfigurationProvider()->isAuthenticationRequired();
		}
		
		protected function isValidPath($path) {
			return count($path) == 0;
		}
		
		public function response() {
			return $this->env->getResponseHandler();
		}
		
		public function processRequest() {
			if (!$this->isValidPath($this->path)) throw new ServiceException("INVALID_REQUEST", "Invalid path: ".$this->request->getURI());
			
			switch($this->request->getMethod()) {
				case 'get':
					$this->processGet();
					break;
				case 'post':
					$this->processPost();
					break;
				default:
					throw new RequestException("Unsupported method '".$this->request->getMethod()."'");
			}
		}
		
		function processGet() { throw new ServiceException("INVALID_REQUEST", "Unimplemented method 'get'"); }
		
		function processPost() { throw new ServiceException("INVALID_REQUEST", "Unimplemented method 'post'"); }
		
		function log() {
			if (!Logging::isDebug()) return;
			$this->request->log();
			Logging::logDebug("SERVICE (".get_class($this)."): is_auth_required=".$this->isAuthenticationRequired($this->request));
		}
	}
?>