<?php

	/**
	 * PublicShareServices.class.php
	 *
	 * Copyright 2008- Samuli Järvelä
	 * Released under GPL License.
	 *
	 * License: http://www.mollify.org/license.php
	 */

	class PublicShareServices extends ServicesBase {		
		protected function isValidPath($method, $path) {
			return TRUE;
		}
		
		public function isAuthenticationRequired() {
			return FALSE;
		}
		
		public function processGet() {
			if (count($this->path) == 1)
				$this->handler()->processShareGet($this->path[0]);
			else if (count($this->path) == 2 && $this->path[1] == "info")
				$this->response()->success($this->handler()->getPublicShareInfo($this->path[0]));
			else
				throw $this->invalidRequestException();
		}
				
		public function processPost() {
			if (count($this->path) != 1) throw $this->invalidRequestException();
			$this->handler()->processSharePost($this->path[0]);
			$this->response()->html(json_encode(array("result" => TRUE)));
		}
		
		private function handler() {
			return $this->env->plugins()->getPlugin("Share")->getHandler();
		}
	}
?>