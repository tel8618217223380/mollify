<?php
	class SessionServices extends ServicesBase {
		private static $ITEMS = array("info");
		
		protected function isValidPath($path) {
			return count($path) == 2 and in_array($path[0], self::$ITEMS);
		}
		
		public function processGet() {
			$this->response()->success($this->getSessionInfo($this->path[1]));
		}
		
		private function getSessionInfo($protocolVersion) {
			Logging::logDebug("Requesting session info for protocol version ".$protocolVersion);
			if (!$this->env->getConfigurationProvider()->isProtocolVersionSupported($protocolVersion)) {
				throw new ServiceException("INVALID_CONFIGURATION", "Unsupported protocol version: [".$protocolVersion."]");
			}
			
			$auth = $this->env->getAuthentication();
			$info = array("authentication_required" => $auth->isAuthenticationRequired(), "authenticated" => $auth->isAuthenticated());
			
			if (!$auth->isAuthenticationRequired() or $auth->isAuthenticated()) {
				$info = array_merge(
					$info,
					$this->env->getSession()->getSessionInfo(),
					$this->env->getAuthentication()->getSessionInfo(),
					$this->env->getFilesystem()->getSessionInfo()
				);
				$info["features"] = $this->env->getFeatures()->getFeatures();
			}
			return $info;
		}
	}
?>