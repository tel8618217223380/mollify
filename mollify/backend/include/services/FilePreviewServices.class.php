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

	class FilePreviewServices extends ServicesBase {
		protected function isValidPath($method, $path) {
			return count($path) == 2;
		}
		
		public function processGet() {
			$item = $this->item($this->path[0]);
			
			if ($this->id === 'preview') {
				if ($this->path[1] === 'html') {
					$this->response()->success($this->env->getObject("preview")->getPreview($item));
					return;
				}
				if ($this->path[1] === 'content') {
					$this->env->filesystem()->download($item);
					return;
				}
			} else if ($this->id === 'view') {
				if ($this->path[1] === 'html') {
					$this->response()->success($this->env->getObject("preview")->getView($item));
					return;
				}
				if ($this->path[1] === 'content') {
					$this->env->filesystem()->download($item);
					return;
				}
			}
			throw $this->invalidRequestException();
		}
		
		public function __toString() {
			return "FilePreviewServices";
		}
	}
?>