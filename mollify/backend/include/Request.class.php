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

	class Request {
		const METHOD_GET = 'get';
		const METHOD_PUT = 'put';
		const METHOD_POST = 'post';
		const METHOD_DELETE = 'delete';
		
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
				case self::METHOD_GET:
					$this->params = $_GET;
					break;
				case self::METHOD_POST:
					$this->params = $_POST;
				case self::METHOD_PUT:
					$data = file_get_contents("php://input");
					if ($data and strlen($data) > 0)
						$this->data = json_decode($data, TRUE);
					break;
				case self::METHOD_DELETE:
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

		public function hasData($key = NULL) {
			if ($key === NULL) return ($this->data != NULL);
			if (!is_array($this->data)) return FALSE;
			return array_key_exists($key, $this->data);
		}
		
		public function data($key) {
			return $this->data[$key];
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