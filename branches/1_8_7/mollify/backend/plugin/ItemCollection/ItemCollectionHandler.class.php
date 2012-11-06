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
			if (count($ic["items"]) == 0) {
				$resourcesUrl = $this->env->getCommonResourcesUrl();
				include("pages/empty.php");
				die();
			}
			if (!$this->env->request()->hasParam("ac")) {
				$resourcesUrl = $this->env->getCommonResourcesUrl();
				include("pages/prepare.php");
				die();
			}
			$type = $this->env->request()->param("ac");
			if (strcmp("prepare", $type) == 0) {
				foreach($ic["items"] as $item)
					$this->env->filesystem()->temporaryItemPermission($item, Authentication::PERMISSION_VALUE_READONLY);
				
				$zip = $this->env->filesystem()->createZip($ic["items"], "ic_".uniqid());
				$this->env->response()->success(array("id" => $zip->name()));
			} else if (strcmp("download", $type) == 0) {
				if (!$this->env->request()->hasParam("id")) {
					Logging::logDebug("Ignoring share request, no zip id provider");
					die();
				}
				
				$id = urldecode($this->env->request()->param("id"));
				$file = sys_get_temp_dir().DIRECTORY_SEPARATOR.$id.'zip';
				if (!file_exists($file)) {
					Logging::logDebug("Zip file missing ".$file);
					die();
				}
				$handle = @fopen($file, "rb");
				if (!$handle)
					throw new ServiceException("REQUEST_FAILED", "Could not open zip for reading: ".$file);
				
				$name = $ic["name"];
				if (!$name or strlen($name) == 0) $name = "items";
				
				$this->env->response()->download($name.".zip", "zip", FALSE, $handle);
				$handle->close();
				unlink($file);
			} else {
				Logging::logDebug("Ignoring share request, invalid share action ".$type);
			}
			die();
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
			else if (strcmp(UserEvent::EVENT_TYPE_USER, $type) == 0 and $subType === UserEvent::USER_REMOVE) {
				$ids = $this->dao()->deleteUserItemCollections($e->id());
				if ($this->env->plugins()->hasPlugin("Share") and count($ids) > 0) {
					foreach($ids as $id)
						$this->env->plugins()->getPlugin("Share")->deleteSharesForItem("ic_".$id);
				}
			}
		}
		
		public function __toString() {
			return "ItemCollectionHandler";
		}
	}
?>