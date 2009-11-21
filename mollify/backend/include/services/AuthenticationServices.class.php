<?php
	class AuthenticationServices extends ServicesBase {
		public function processGet() {
			$state = (!$this->env->getAuthentication()->isAuthenticationRequired() or $this->env->getAuthentication()->isAuthenticated());
			$this->response()->success($state);
		}
	}
?>