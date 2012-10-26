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
	
	require_once("dao/ItemCollectionDao.class.php");
	
	class ItemCollectionHandler {
		private $env;
		private $settings;
		
		public function __construct($env, $settings) {
			$this->env = $env;
			$this->settings = $settings;
		}
		
		public function processGetShare($id, $share) {
			$ic = $this->dao()->getItemCollection($id);
			if (!$ic) die("No collection found");
			
			die("IC ".$ic["name"]);
		}
		
		public function getUserItemCollections() {
			return $this->dao()->getUserItemCollections($this->env->session()->userId());
		}

		public function addUserItemCollection($name, $items) {
			$created = $this->env->configuration()->formatTimestampInternal(time());
			$this->dao()->addUserItemCollection($this->env->session()->userId(), $name, $items, $created);
		}
		
		public function deleteUserItemCollection($id) {
			$this->dao()->deleteUserItemCollection($id, $this->env->session()->userId());
			if ($this->env->plugins()->hasPlugin("Share")) $this->env->plugins()->getPlugin("Share")->deleteSharesForItem("ic_".$id);
		}
		
		private function dao() {
			return new ItemCollectionDao($this->env);
		}

		public function onEvent($e) {
			if (strcmp(FilesystemController::EVENT_TYPE_FILE, $e->type()) != 0) return;
			$type = $e->subType();
			
			if ($type === FileEvent::DELETE)
				$this->dao()->deleteCollectionItems($e->item());
		}
		
		public function __toString() {
			return "ItemCollectionHandler";
		}
	}
?>