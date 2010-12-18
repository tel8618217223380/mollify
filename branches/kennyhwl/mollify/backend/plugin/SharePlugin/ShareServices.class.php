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

	class ShareServices extends ServicesBase {
		protected function isValidPath($method, $path) {
			return count($path) == 1;
		}
		
		public function isAuthenticationRequired() {
			return TRUE;
		}
		
		public function processPost() {
			$item = $this->item($this->path[0]);			
			$data = $this->request->data;
			
			if (!isset($data['users']) or !isset($data['permission'])) throw $this->invalidRequestException();
			
			foreach($data['users'] as $id) {
				$userFolder = $this->getUserFolder($id);
				if (!$userFolder) {
					Logging::logDebug("User folder not found for user ".$id);
					continue;
				}
				
				$target = $this->shareTo($item, $userFolder);
				if ($target != NULL) {
					$this->env->configuration()->addItemPermission($target->id(), $data['permission'], $id);
					Logging::logDebug("Item ".$item->id()." shared with user ".$id);
				} else {
					Logging::logDebug("Item ".$item->id()." not shared with user ".$id);
				}
			}

			$this->response()->success(array());
		}
		
		private function getUserFolder($userId) {
			$folders = $this->env->configuration()->getUserFolders($userId);
			if (!$folders or count($folders) == 0) return NULL;
			
			$folder = $folders[0];
			$target = $this->env->filesystem()->filesystemFromId($folder["id"]);
			return $target->root();
		}
		
		private function shareTo($item, $to) {
			if ($item->isFile()) $target = $to->fileWithName($item->name(), TRUE);
			else $target = $to->folderWithName($item->name(), TRUE);
			
			if (!symlink($item->internalPath(), $target->internalPath())) return NULL;
			return $target;
		}
		
		public function __toString() {
			return "ShareServices";
		}
	}
?>