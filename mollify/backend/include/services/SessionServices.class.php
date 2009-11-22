<?php
	class SessionServices extends ServicesBase {
		private static $PROTOCOL_VERSION = "1_5_0";
		private static $GET_ITEMS = array("info");
		private static $POST_ITEMS = array("authenticate", "logout");
		
		protected function isValidPath($method, $path) {
			if (count($path) < 1) return FALSE;
			
			if ($method === Request::$METHOD_GET and !in_array($path[0], self::$GET_ITEMS)) return FALSE;
			if ($method === Request::$METHOD_POST and !in_array($path[0], self::$POST_ITEMS)) return FALSE;
			if ($path[0] === 'info' and count($path) < 2) return FALSE;
			
			return TRUE;
		}
		
		public function isAuthenticationRequired() {
			return FALSE;
		}

		public function processGet() {
			$this->response()->success($this->getSessionInfo($this->path[1]));
		}

		public function processPost() {
			if ($this->path[0] === 'logout') {
				$this->env->session()->reset();
				$this->response()->success(TRUE);
				return;
			}
			
			$this->authenticate();
		}
		
		private function authenticate() {
			if (!$this->request->hasParam("username") or !$this->request->hasParam("password") or !$this->request->hasParam("protocol_version"))
				throw new ServiceException("INVALID_REQUEST", "Missing parameters");
			
			$this->env->authentication()->authenticate($this->request->param("username"), $this->request->param("password"));
			
			$this->response()->success($this->getSessionInfo($this->request->param("protocol_version")));
		}
		
		private function getSessionInfo($protocolVersion) {
			Logging::logDebug("Requesting session info for protocol version ".$protocolVersion);
			if ($protocolVersion != self::$PROTOCOL_VERSION)
				throw new ServiceException("INVALID_CONFIGURATION", "Unsupported protocol version [".$protocolVersion."], expected [".self::$PROTOCOL_VERSION."]");
			$this->env->configuration()->checkProtocolVersion($protocolVersion);
			
			$auth = $this->env->authentication();
			$info = array("authentication_required" => $auth->isAuthenticationRequired(), "authenticated" => $auth->isAuthenticated());
			
			if (!$auth->isAuthenticationRequired() or $auth->isAuthenticated()) {
				$info = array_merge(
					$info,
					$this->env->session()->getSessionInfo(),
					$this->env->authentication()->getSessionInfo(),
					$this->env->filesystem()->getSessionInfo()
				);
				$info["features"] = $this->env->features()->getFeatures();
			}
			return $info;
		}
	}
?>