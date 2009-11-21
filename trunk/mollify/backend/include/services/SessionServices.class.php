<?php
	class SessionServices extends ServicesBase {
		private static $ITEMS = array("info");
		
		protected function isValidPath($path) {
			return count($path) == 1 and in_array($path[0], self::$ITEMS);
		}
		
		public function processGet() {
			$this->response()->success($this->getSessionInfo());
		}
		
		private function getSessionInfo() {
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