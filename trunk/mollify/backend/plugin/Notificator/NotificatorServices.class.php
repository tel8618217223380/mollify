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
			
			$db = $this->env->configuration()->db();
			$result = $db->query("select `id`, `name` from ".$db->table("notificator_notifications")." order by id asc")->rows();
			$this->response()->success($result);
		}
				
		public function __toString() {
			return "NotificatorServices";
		}
	}
?>