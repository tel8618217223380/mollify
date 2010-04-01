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
		
		private $view;
		private $preview;
		
		public function __construct($serviceEnvironment, $view, $preview) {
			$this->env = $serviceEnvironment;
			$this->view = $view;
			$this->preview = $preview;
		}
		
		public function getItemDetails($item, $details) {
			if (!$item->isFile()) return FALSE;
			
			$type = strtolower($item->extension());
			if (!in_array($type, self::$previewTypes)) return FALSE;
			
			$html = '<div id="preview-container" style="overflow:auto; max-height:300px"><img src="'.$_SERVER['SCRIPT_NAME']."/preview/".$item->id().'" style="max-width:400px"></div>';
			
			return array(
				"preview" => array(
					"html" => $html
				)
			);
		}
		
		public function __toString() {
			return "FilePreview";
		}
	}
?>