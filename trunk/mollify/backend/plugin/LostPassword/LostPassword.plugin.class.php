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
	
	class LostPassword extends PluginBase {
		public function setup() {
			$this->addService("lostpassword", "LostPasswordServices");
			$this->env->features()->addFeature("lost-password");
		}
				
		public function isConfigurationSupported($type) {
			return $type === ConfigurationProvider::TYPE_DATABASE;
		}
		
		public function version() {
			return "1_0";
		}

		public function versionHistory() {
			return array("1_0");
		}
				
		public function __toString() {
			return "LostPasswordPlugin";
		}
	}	
?>