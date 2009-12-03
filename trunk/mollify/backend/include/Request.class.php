<?php
	class Request {
		public static $METHOD_GET = 'get';
		public static $METHOD_PUT = 'put';
		public static $METHOD_POST = 'post';
		public static $METHOD_DELETE = 'delete';
		
		private $method;
		private $uri;
		private $parts;
		private $params;
		
		public function __construct() {
			$this->method = strtolower($_SERVER['REQUEST_METHOD']);
			$this->uri = trim($_SERVER['PATH_INFO'], "/");
			$this->parts = explode("/", $this->uri);
			$this->params = array();
			$this->data = NULL;
			
			switch($this->method) {
				case self::$METHOD_GET:
					$this->params = $_GET;
					break;
				case self::$METHOD_POST:
					$this->params = $_POST;
				case self::$METHOD_PUT:
					$data = file_get_contents("php://input");
					if ($data and strlen($data) > 0)
						$this->data = json_decode($data, TRUE);
					break;
				case self::$METHOD_DELETE:
					break;
				default:
					throw new Exception("Unsupported method: ".$this->method);
			}
		}
		
		public function method() {
			return $this->method;
		}
		
		public function URI() {
			return $this->uri;
		}
		
		public function path() {
			return $this->parts;
		}
		
		public function params() {
			return $this->params;
		}
		
		public function hasParam($param) {
			return array_key_exists($param, $this->params);
		}
		
		public function param($param) {
			return $this->params[$param];
		}
		
		public function log() {
			Logging::logDebug("REQUEST: method=".$this->method.", path=".Util::array2str($this->parts).", params=".Util::array2str($this->params).", data=".Util::toString($this->data));
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
		
		public function code() {
			return $this->code;
		}

		public function type() {
			return $this->type;
		}
		
		public function data() {
			return $this->data;
		}
	}
?>