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
	
	class Notificator extends PluginBase {
		public function hasAdminView() {
			return TRUE;
		}

		public function setup() {
			if (!$this->env->features()->isFeatureEnabled("mail_notification")) return;
			$this->addService("notificator", "NotificatorServices");
			
			require_once("NotificatorHandler.class.php");
			$this->env->events()->register("*", new Notificator($this->env));
		}
		
		public function __toString() {
			return "NotificatorPlugin";
		}
	}
?>