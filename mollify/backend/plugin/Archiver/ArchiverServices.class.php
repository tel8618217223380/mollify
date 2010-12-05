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
			return count($path) == 2;
		}
		
		public function isAuthenticationRequired() {
			return TRUE;
		}
		
		public function processPost() {
			$itemId = $this->path[0];
			$action = $this->path[1];
			if ($action !== 'extract') throw $this->invalidRequestException();
			
			$archive = $this->item($itemId);
			$folder = $archive->parent();
			$target = $folder->internalPath().DIRECTORY_SEPARATOR.basename($archive->internalPath());
			
			if (file_exists($target))
				throw new ServiceException("FOLDER_ALREADY_EXISTS", $target);
			
			mkdir($target);
			
			require_once("ArchiveManager.class.php");
			$a = new ArchiveManager();
			$a->extract($archive->internalPath(), $target);

			$this->response()->success(array());
		}
						
		public function __toString() {
			return "ArchiverServices";
		}
	}
?>