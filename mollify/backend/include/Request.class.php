<?php
	class Request {
		private $method;
		private $uri;
		private $parts;
		private $params;
		
		public function __construct() {
			$this->method = strtolower($_SERVER['REQUEST_METHOD']);
			$this->uri = trim($_SERVER['PATH_INFO'], "/");
			$this->parts = explode("/", $this->uri);
			
			switch($this->method) {
				case 'get':
					$this->params = $_GET;
					break;
				case 'post':
					$this->params = $_POST;
					break;
				default:
					throw new Exception("Unsupported method");
			}
		}
		
		public function getMethod() {
			return $this->method;
		}
		
		public function getURI() {
			return $this->uri;
		}
		
		public function getParts() {
			return $this->parts;
		}
		
		public function getParams() {
			return $this->params;
		}
		
		public function hasParam($param) {
			return array_key_exists($param, $this->params);
		}
		
		public function getParam($param) {
			return $this->params[$param];
		}
		
		public function log() {
			Logging::logDebug("REQUEST: method=".$this->method.", path=".Util::array2str($this->parts).", params=".Util::array2str($this->params));
		}
	}
	
	class Response {
		private $code;
		private $type;
		private $data;
		
		public function __construct($code, $type, $data) {
			$this->code = $code;
			$this->type = $type;
			$this->data = $data;
		}
		
		public function getCode() {
			return $this->code;
		}

		public function getType() {
			return $this->type;
		}
		
		public function getData() {
			return $this->data;
		}
	}
?>