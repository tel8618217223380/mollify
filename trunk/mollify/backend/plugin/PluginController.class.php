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
			
			foreach($PLUGINS as $p => $settings)
				$this->addPlugin($p, $settings);
			
			foreach($this->plugins as $id => $p)
				$p->setup();
		}
		
		private function addPlugin($id, $settings) {
			require_once($id."/".$id.".plugin.class.php");
			$p = new $id($this->env, $id, $settings);
			if (!$p->isConfigurationSupported($this->env->configuration()->getType()))
				return;
			$this->plugins[$id] = $p;
		}
		
		public function getPlugins() {
			return $this->plugins;
		}
		
		public function getSessionInfo() {
			$result = array();
			foreach($this->plugins as $id => $p) {
				$result[$id] = $p->getSessionInfo();
			}
			return $result;
		}
		
		public function initialize($request) {}
		
		public function __toString() {
			return "PluginController";
		}
	}
?>