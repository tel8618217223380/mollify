<?php

	/**
	 * Copyright (c) 2008- Samuli Järvelä
	 *
	 * All rights reserved. This program and the accompanying materials
	 * are made available under the terms of the Eclipse Public License v1.0
	 * which accompanies this distribution, and is available at
	 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
	 * this entire header must remain intact.
	 */

	class ShareServices extends ServicesBase {
		protected function isValidPath($method, $path) {
			return count($path) == 1;
		}
		
		public function isAuthenticationRequired() {
			return TRUE;
		}
		
		public function processPost() {
			$item = $this->item($this->path[0]);			
			$data = $this->request->data;
			
			if (!isset($data['users']) or !isset($data['permission'])) throw $this->invalidRequestException();

			$this->response()->success(array());
		}
						
		public function __toString() {
			return "ShareServices";
		}
	}
?>