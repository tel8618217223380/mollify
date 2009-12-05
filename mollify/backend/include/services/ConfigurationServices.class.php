<?php
	class ConfigurationServices extends ServicesBase {
		protected function isValidPath($method, $path) {
			if ($method === Request::METHOD_GET and count($path) == 1) return TRUE;
			return FALSE;
		}
		
		public function processGet() {
			if (!in_array($this->path[0], array("users"))) throw $this->invalidRequestException();

			$this->response()->success($this->env->configuration()->getAllUsers());
		}
	}
?>