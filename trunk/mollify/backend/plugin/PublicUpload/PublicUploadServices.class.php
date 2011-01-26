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

	class PublicUploadServices extends ServicesBase {		
		protected function isValidPath($method, $path) {
			return TRUE;
		}
		
		public function isAuthenticationRequired() {
			return FALSE;
		}
		
		public function processPost() {
			if (count($this->path) != 0) throw $this->invalidRequestException();
			if (!$this->request->hasParam("id")) throw $this->invalidRequestException();
			
			throw $this->invalidRequestException();
		}
	}