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

	abstract class FullDocumentEditor extends EditorBase {
		public function getInfo($item) {
			return array(
				"embedded" => $this->getDataUrl($item, "embedded"),
				"full" => $this->getDataUrl($item, "full", TRUE)
			);
		}
		
		public function processDataRequest($item, $path) {
			if (count($path) != 1) throw $this->invalidRequestException();
			
			if ($path[0] === 'edit')
				$this->processEditRequest($item);
			else if ($path[0] === 'embedded')
				$this->processEmbeddedEditRequest($item);
			else
				throw $this->invalidRequestException();
		}

		protected function processEmbeddedEditRequest($item) {
			$html = '<iframe id="editor-frame" src="'.$this->getDataUrl($item, "edit", TRUE).'&embedded=true" style="border: none;"></iframe>';
			$size = $this->getEmbeddedSize();
			 
			$this->response()->success(array(
				"html" => $html,
				"resized_element_id" => "editor-frame",
				"size" => $size[0].";".$size[1]
			));
		}
		
		protected function getEmbeddedSize() {
			return array("600", "400");
		}
		
		protected abstract function getHtml($item, $full);
		
		protected function processEditRequest($item) {
			$full = $this->request()->hasParam("embedded") and (strcasecmp("true", $this->request()->param("embedded")) == 0);
			$this->response()->html($this->getHtml($item, $full));
		}
	}
?>