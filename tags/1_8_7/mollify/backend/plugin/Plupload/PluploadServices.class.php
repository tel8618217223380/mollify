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

	class PluploadServices extends ServicesBase {
		public function isAuthenticationRequired() {
			return TRUE;
		}
		
		protected function isValidPath($method, $path) {
			return count($path) == 1 or count($path) == 2;
		}
				
		public function processPost() {
			if (count($this->path) == 2 and strcmp("check", $this->path[1]) != 0) throw $this->invalidRequestException();
			
			$folder = $this->item($this->path[0]);
			if ($folder->isFile()) throw $this->invalidRequestException("Target not a folder");
			
			$this->env->features()->assertFeature("file_upload");
			$this->env->filesystem()->assertRights($folder, Authentication::RIGHTS_WRITE, "upload");

			$h = $this->getHandler();
			
			if (count($this->path) == 2) {
				if (!isset($this->request->data["files"])) throw $this->invalidRequestException();
				$existing = $h->checkUpload($folder, $this->request->data["files"]);
				$this->response()->success(array("ok" => (count($existing) == 0), "existing" => $existing));
				return;
			}
			
			$h->uploadTo($folder, $this->env->filesystem()->getUploadTempDir(), $this->env->filesystem());
			$this->response()->html(json_encode(array("result" => TRUE)));
			die();
		}
		
		private function getHandler() {
			$p = $this->env->plugins()->getPlugin("Plupload");
			return $p->getHandler();
		}
	}
?>