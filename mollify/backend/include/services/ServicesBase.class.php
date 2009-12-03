<?php
	class ServicesBase {
		protected $env;
		protected $request;
		protected $path;
		
		public function __construct($serviceEnvironment, $request, $path) {
			$this->env = $serviceEnvironment;
			$this->request = $request;
			$this->path = $path;
			
			if (!$this->isValidPath($this->request->method(), $this->path)) throw new ServiceException("INVALID_REQUEST", "Invalid path: ".strtoupper($this->request->method())." ".$this->request->URI());
		}
		
		public function isAuthenticationRequired() {
			return $this->env->configuration()->isAuthenticationRequired();
		}
		
		protected function isValidPath($method, $path) {
			return count($path) == 0;
		}
		
		public function response() {
			return $this->env->response();
		}
		
		public function processRequest() {
			switch($this->request->method()) {
				case Request::$METHOD_GET:
					$this->processGet();
					break;
				case Request::$METHOD_PUT:
					$this->processPut();
					break;
				case Request::$METHOD_POST:
					$this->processPost();
					break;
				case Request::$METHOD_DELETE:
					$this->processDelete();
					break;
				default:
					throw new RequestException("Unsupported method '".$this->request->method()."'");
			}
		}
		
		function processGet() { throw new ServiceException("INVALID_REQUEST", "Unimplemented method 'get'"); }
		
		function processPut() { throw new ServiceException("INVALID_REQUEST", "Unimplemented method 'put'"); }
		
		function processPost() { throw new ServiceException("INVALID_REQUEST", "Unimplemented method 'post'"); }
		
		function processDelete() { throw new ServiceException("INVALID_REQUEST", "Unimplemented method 'delete'"); }
		
		function log() {
			if (!Logging::isDebug()) return;
			$this->request->log();
			Logging::logDebug("SERVICE (".get_class($this)."): is_auth_required=".$this->isAuthenticationRequired($this->request));
		}
	}
?>