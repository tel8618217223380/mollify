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
			return count($path) == 0;
		}
		
		public function isAuthenticationRequired() {
			return FALSE;
		}
		
		public function processPost() {
			if (!$this->request->hasParam("id")) throw $this->invalidRequestException();
			
			$this->env->filesystem()->allowFilesystems = TRUE;
			$folder = $this->item($this->request->param("id"));
			Logging::logDebug("Public upload to: ".$folder->id());
			$this->env->filesystem()->temporaryItemPermission($folder, Authentication::PERMISSION_VALUE_READWRITE);
			$this->env->filesystem()->uploadTo($folder);
		}
	}