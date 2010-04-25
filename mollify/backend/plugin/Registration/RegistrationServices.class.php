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

	class RegistrationServices extends ServicesBase {
		protected function isValidPath($method, $path) {
			return count($path) <= 1;
		}
		
		public function isAuthenticationRequired() {
			return FALSE;
		}
		
		public function processGet() {
			if (count($this->path) == 1 and $this->path[0] === 'confirm') {
				//TODO check key
				//TODO if correct, activate account
			}
			throw $this->invalidRequestException();
		}
		
		public function processPost() {
			$registration = $this->request->data;
			if (!isset($registration['name']) or !isset($registration['password']) or !isset($registration['email'])) throw $this->invalidRequestException();
			
			//TODO check name & email
			//TODO store pending account (create uuid)
			//TODO send confirm email with link (uuid)
			//TODO handle confirm link
		}
		
		public function __toString() {
			return "RegistrationServices";
		}
	}
?>