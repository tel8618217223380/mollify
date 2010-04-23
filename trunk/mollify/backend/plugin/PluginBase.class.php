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

	abstract class PluginBase {
		protected $env;
		protected $id;
		
		public function __construct($env, $id) {
			$this->env = $env;
			$this->id = $id;
		}
		
		public abstract function setup();
		
		public function id() {
			return $this->id;
		}
		
		public function addService($path, $controller) {
			$this->env->addService($path, $controller, "plugin/".$this->id."/");
		}
		
		function log() {
			if (!Logging::isDebug()) return;
			Logging::logDebug("PLUGIN (".get_class($this).")");
		}
	}
?>