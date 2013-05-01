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

	class ArchiverServices extends ServicesBase {
		protected function isValidPath($method, $path) {
			return count($path) > 0;
		}
		
		public function isAuthenticationRequired() {
			return TRUE;
		}
		
		//GET zipped
		
		public function processPost() {
			$action = $this->path[0];
			
			if (!in_array($action, array("extract", "compress", "pack"))) throw $this->invalidRequestException();
			
			if ($action === 'extract') $this->onExtract();
			else if ($action === 'compress') $this->onCompress();
			else $this->onPack();
		}
		
		private function onCompress() {
			$data = $this->request->data;
			$items = $data['items'];
			if (count($items) < 1 || !array_key_exists("folder", $data) || !array_key_exists("name", $data) || strlen($data["name"]) == 0) throw $this->invalidRequestException();
		
			$folder = $this->item($data['folder']);
			$this->env->filesystem()->assertRights($folder, Authentication::RIGHTS_WRITE, "compress");
			
			$items = array();
			foreach($data['items'] as $i)
				$items[] = $this->item($i);
			if (count($items) == 0) throw $this->invalidRequestException();
			
			$this->env->filesystem()->assertRights($items, Authentication::RIGHTS_READ, "compress");
			
			$ext = ".zip";	//TODO
			$extl = strlen($ext);
			
			$name = $data["name"];
			if (strlen($name) > $extl && substr($name, -$extl) === $ext) $name = substr($name, 0, strlen($name)-$extl);
			
			$name = str_replace(".", "_", $name);
			$target = rtrim($folder->internalPath(), DIRECTORY_SEPARATOR).DIRECTORY_SEPARATOR.$name.$ext;
			
			if (file_exists($target)) {
				if (!$overwrite)
					throw new ServiceException("FILE_ALREADY_EXISTS", $target);
				$parent->fileWithName($name)->delete();
			}

			$archiveId = $this->archiveManager()->compress($items, $target);
			$this->response()->success(array());
		}
		
		private function onExtract() {
			$data = $this->request->data;
			$itemId = $data["item_id"];
			
			$overwrite = isset($data['overwrite']) ? $data['overwrite'] : FALSE;
			$archive = $this->item($itemId);
			$this->env->filesystem()->assertRights($archive, Authentication::RIGHTS_READ, "extract");
			
			$parent = $archive->parent();
			$this->env->filesystem()->assertRights($parent, Authentication::RIGHTS_WRITE, "extract");
			
			$name = str_replace(".", "_", basename($archive->internalPath()));
			$target = $parent->internalPath().DIRECTORY_SEPARATOR.$name.DIRECTORY_SEPARATOR;
			
			if (file_exists($target)) {
				if (!$overwrite)
					throw new ServiceException("DIR_ALREADY_EXISTS", $target);
				$parent->folderWithName($name)->delete();
			}
			
			mkdir($target);
			
			$this->archiveManager()->extract($archive->internalPath(), $target);
			$this->response()->success(array());
		}

		/*private function onCompress($itemId) {
			$data = $this->request->data;
			$overwrite = isset($data['overwrite']) ? $data['overwrite'] : FALSE;

			$folder = $this->item($itemId);
			if ($folder->isFile()) throw $this->invalidRequestException();
			$this->env->filesystem()->assertRights($folder, Authentication::RIGHTS_READ, "compress");
			
			$parent = $folder->parent();
			$this->env->filesystem()->assertRights($parent, Authentication::RIGHTS_WRITE, "compress");
			
			$name = str_replace(".", "_", basename($folder->internalPath()));
			$target = $parent->internalPath().DIRECTORY_SEPARATOR.$name.".zip";
			
			if (file_exists($target)) {
				if (!$overwrite)
					throw new ServiceException("FILE_ALREADY_EXISTS", $target);
				$parent->fileWithName($name)->delete();
			}
			
			$this->archiveManager()->compress($folder, $target);
			$this->response()->success(array());
		}*/
		
		private function archiveManager() {
			return $this->env->plugins()->getPlugin("Archiver")->getArchiveManager();
		}
					
		public function __toString() {
			return "ArchiverServices";
		}
	}
?>