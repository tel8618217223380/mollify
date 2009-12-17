<?php

	/**
	 * Copyright (c) 2008- Samuli JŠrvelŠ
	 *
	 * All rights reserved. This program and the accompanying materials
	 * are made available under the terms of the Eclipse Public License v1.0
	 * which accompanies this distribution, and is available at
	 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
	 * this entire header must remain intact.
	 */

	class FilesystemServices extends ServicesBase {
		protected function isValidPath($method, $path) {
			if (count($path) < 1 or count($path) > 3) return FALSE;
			return TRUE;
		}
		
		public function processGet() {
			if ($this->path[0] === 'upload') {
				$this->processGetUpload();
				return;
			}
			$item = $this->env->filesystem()->item($this->convertItemID($this->path[0]));
			
			if ($item->isFile()) $this->processGetFile($item);
			else $this->processGetFolder($item);
		}

		public function processPut() {
			if ($this->path[0] === 'permissions') {
				$this->env->authentication()->assertAdmin();
				$this->response()->success($this->env->configuration()->updateItemPermissions($this->request->data));
				return;
			}
			
			$item = $this->env->filesystem()->getItemFromId($this->convertItemID($this->path[0]));
			
			if ($item->isFile()) $this->processPutFile($item);
			else $this->processPutFolder($item);
		}
		
		public function processPost() {
			$item = $this->env->filesystem()->getItemFromId($this->convertItemID($this->path[0]));
			
			if ($item->isFile()) $this->processPostFile($item);
			else $this->processPostFolder($item);
		}
		
		public function processDelete() {
			if (count($this->path) != 1) throw invalidRequestException();

			$item = $this->env->filesystem()->getItemFromId($this->convertItemID($this->path[0]));
			$item->delete();
			$this->response()->success(TRUE);
		}
		
		private function convertItemId($id) {
			return strtr($id, '-_,', '+/=');
		}
				
		private function processGetFile($item) {
			if (count($this->path) == 1) {
				$item->download();
				return;
			}
						
			switch (strtolower($this->path[1])) {
				case 'zip':
					$item->downloadAsZip();
					return;
				case 'details':
					$this->response()->success($item->details());
					break;
				case 'permissions':
					$this->response()->success($item->allPermissions());
					break;
				default:
					throw $this->invalidRequestException();
			}
		}
		
		private function processPutFile($item) {
			if (count($this->path) != 2) throw invalidRequestException();
						
			switch (strtolower($this->path[1])) {
				case 'name':
					$item->rename($this->request->data);
					$this->response()->success(TRUE);
					break;
				case 'description':
					$item->setDescription($this->request->data);
					$this->response()->success(TRUE);
					break;
				default:
					throw $this->invalidRequestException();
			}
		}
		
		private function processPostFile($item) {
			if (count($this->path) != 2) throw $this->invalidRequestException();
			
			switch (strtolower($this->path[1])) {
				case 'move':
					$item->move($this->env->filesystem()->getItemFromId($this->request->data));
					break;
				case 'copy':
					$item->copy($this->env->filesystem()->getItemFromId($this->request->data));
					break;
				default:
					throw $this->invalidRequestException();
			}
			
			$this->response()->success(TRUE);
		}
				
		private function processGetFolder($item) {
			if (count($this->path) != 2) throw invalidRequestException();
			
			switch (strtolower($this->path[1])) {
				case 'zip':
					$item->downloadAsZip();
					return;
				case 'items':
					$this->response()->success(array("folders" => $item->folders(), "files" => $item->files()));
					break;
				case 'files':
					$this->response()->success($item->files());
					break;
				case 'folders':
					$this->response()->success($item->folders());
					break;
				case 'details':
					$this->response()->success($item->details());
					break;
				default:
					throw $this->invalidRequestException();
			}
		}
		
		private function processPutFolder($item) {
			if (count($this->path) != 2) throw invalidRequestException();
						
			switch (strtolower($this->path[1])) {
				case 'name':
					$item->rename($this->request->data);
					$this->response()->success(TRUE);
					break;
				case 'description':
					$item->setDescription($this->request->data);
					$this->response()->success(TRUE);
					break;
				default:
					throw $this->invalidRequestException();
			}
		}
		
		private function processPostFolder($item) {
			if (count($this->path) != 2) throw $this->invalidRequestException();
			
			switch (strtolower($this->path[1])) {
				case 'files':
					$item->uploadTo();
					$this->response()->success(TRUE);
					break;
				case 'folders':
					$item->createFolder($this->request->data);
					$this->response()->success(TRUE);
					break;
				case 'move':
					$item->move($this->env->filesystem()->getItemFromId($this->request->data));
					break;
				default:
					throw $this->invalidRequestException();
			}
		}
		
		private function processGetUpload() {
			if (count($this->path) != 3 or $this->path[2] != 'status') throw invalidRequestException();
			$this->env->features()->assertFeature("file_upload_progress");
			Logging::logDebug('upload status '.$this->path[1]);
			$this->response()->success(apc_fetch('upload_'.$this->path[1]));
		}
	}
?>