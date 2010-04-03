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

	class FilePreview {
		static $previewTypes = array("gif", "png", "jpg");
		static $viewTypes = array("gif", "png", "jpg");
		
		private $view;
		private $preview;
		
		public function __construct($serviceEnvironment, $view, $preview) {
			$this->env = $serviceEnvironment;
			$this->view = $view;
			$this->preview = $preview;
		}
		
		public function getItemDetails($item) {
			if (!$item->isFile()) return FALSE;
			$type = strtolower($item->extension());
			
			$result = array();
			if ($this->preview and in_array($type, self::$previewTypes)) {
				$result["preview"] = array(
					"url" => $this->env->getServiceUrl("preview", array($item->id(), "embedded"))
				);
			}
			if ($this->preview and in_array($type, self::$viewTypes)) {
				$result["view"] = array(
					"embedded" => $this->env->getServiceUrl("view", array($item->id(), "embedded"), TRUE),
					"full" => $this->env->getServiceUrl("view", array($item->id(), "full"), TRUE)
				);
			}
			
			return $result;
		}
		
		public function getPreview($item) {
			$dataUrl = $this->env->getServiceUrl("preview", array($item->id(), "content"), TRUE);
			return array("html" => '<div id="file-preview-container" style="overflow:auto; max-height:300px"><img src="'.$dataUrl.'" style="max-width:400px"></div>');
		}
		
		public function getView($item, $full) {
			$dataUrl = $this->env->getServiceUrl("view", array($item->id(), "content"), TRUE);
			
			$html = $full ? "<html>" : "";
			$html .= '<img src="'.$dataUrl.'">';
			if ($full) $html .= "</html>";
			return $html;
		}
		
		public function __toString() {
			return "FilePreview";
		}
	}
?>