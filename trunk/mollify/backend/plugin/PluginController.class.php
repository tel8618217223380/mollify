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
	 
	require_once("PluginBase.class.php");

	class PluginController {
		protected $env;
		protected $plugins;
		
		public function __construct($env) {
			$this->env = $env;
			$this->plugins = array();
		}
		
		public function setup() {
			global $PLUGINS;
			if (!isset($PLUGINS) or !is_array($PLUGINS)) return;
			
			foreach($PLUGINS as $p)
				$this->addPlugin($p);
			
			foreach($this->plugins as $id => $p)
				$p->setup();
		}
		
		private function addPlugin($plugin) {
			require_once($plugin.".plugin.class.php");
			$p = new $plugin($this->env);
			$this->plugins[$p->id()] = $p;
		}
		
		public function initialize($request) {}
		
		public function __toString() {
			return "PluginController";
		}
	}
?>