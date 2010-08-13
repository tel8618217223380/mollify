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

	class NotificatorServices extends ServicesBase {
		protected function isValidPath($method, $path) {
			return count($path) == 1;
		}

		public function processGet() {
			if (count($this->path) != 1 or $this->path[0] != 'list') throw $this->invalidRequestException();
			$this->env->authentication()->assertAdmin();
			
			require_once("dao/NotificatorDao.class.php");
			
			$dao = new NotificatorDao($this->env);
			$this->response()->success($dao->getAllNotifications());
		}
				
		public function __toString() {
			return "NotificatorServices";
		}
	}
?>