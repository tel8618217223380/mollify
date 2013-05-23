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
	
	require_once("ItemCollectionHandler.class.php");
	require_once("ItemCollectionServices.class.php");
	
	class ItemCollection extends PluginBase {
		private $handler;
		
		public function version() {
			return "1_0";
		}

		public function versionHistory() {
			return array("1_0");
		}
		
		public function setup() {
			$this->env->features()->addFeature("itemcollection");
			$this->addService("itemcollections", "ItemCollectionServices");
			
			$this->handler = new ItemCollectionHandler($this->env, $this->getSettings());
			$this->env->events()->register("filesystem/", $this->handler);
			$this->env->events()->register("user/", $this->handler);
			if ($this->env->plugins()->hasPlugin("Share")) $this->env->plugins()->getPlugin("Share")->registerHandler("ic", $this->handler);
		}
				
		public function getHandler() {
			return $this->handler;
		}
				
		public function __toString() {
			return "ItemCollectionPlugin";
		}
	}
?>
