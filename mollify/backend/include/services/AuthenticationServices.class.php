<?php
	class AuthenticationServices extends ServicesBase {
		protected function isValidPath($method, $path) {
			return ($method == Request::METHOD_GET and count($path) == 0);
		}
		
		public function processGet() {
			$state = (!$this->env->authentication()->isAuthenticationRequired() or $this->env->authentication()->isAuthenticated());
			$this->response()->success($state);
		}
	}
?>