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

	class TextFileViewer extends ViewerBase {
		private $env;
		
		public function __construct($env) {
			$this->env = $env;
		}
		
		public function getInfo($item) {
			return array(
				"embedded" => $this->env->getViewUrl($item, array("embedded")),
				"full" => $this->env->getViewUrl($item, array("full"))
			);
		}
		
		public function processDataRequest($item, $path) {
			$html = '<iframe id="text-file-viewer" src="'.$this->getContentUrl($item).'" style="border: none;"></iframe>';
			return array(
				"html" => $html,
				"resized_element_id" => "text-file-viewer",
				"size" => "600;400"
			);
		}
		
		private function getContentUrl($item) {
			return $this->env->getServiceUrl("view", array($item->id(),"content"), TRUE);
		}
	}
?>