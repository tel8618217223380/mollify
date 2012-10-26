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
	require_once("include/configuration/UserEvent.class.php");
	
	class ItemCollectionHandler {
		private $env;
		private $settings;
		
		public function __construct($env, $settings) {
			$this->env = $env;
			$this->settings = $settings;
		}
		
		public function processGetShare($id, $share) {
			$ic = $this->dao()->getItemCollection($id);
			if (!$ic) {
				Logging::logDebug("Ignoring share request, no item collection found with id ".$id);
				die();
			}
			$this->env->filesystem()->downloadAsZip($ic["items"]);
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
			$type = $e->type();
			$subType = $e->subType();
			
			if (strcmp(FilesystemController::EVENT_TYPE_FILE, $type) == 0 and $subType === FileEvent::DELETE)
				$this->dao()->deleteCollectionItems($e->item());
			else if (strcmp(UserEvent::EVENT_TYPE_USER, $type) == 0 and $subType === UserEvent::USER_REMOVE)
				$this->dao()->deleteUserItemCollections($e->id());
		}
		
		public function __toString() {
			return "ItemCollectionHandler";
		}
	}
?>