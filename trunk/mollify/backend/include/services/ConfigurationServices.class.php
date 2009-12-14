<?php

	/**
	 * Copyright (c) 2008- Samuli JŠrvelŠ
	 *
	 * All rights reserved. This program and the accompanying materials
	 * are made available under the terms of the Eclipse Public License v1.0
	 * which accompanies this distribution, and is available at
	 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
	 * this entire header must remain intact.
	 */

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