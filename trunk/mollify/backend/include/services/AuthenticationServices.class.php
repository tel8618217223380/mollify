<?php
	class AuthenticationServices extends ServicesBase {
		public function processGet() {
			$state = (!$this->env->authentication()->isAuthenticationRequired() or $this->env->authentication()->isAuthenticated());
			$this->response()->success($state);
		}
	}
?>