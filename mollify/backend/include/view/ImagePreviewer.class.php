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

	class ImagePreviewer {
		private $env;
		
		public function __construct($env) {
			$this->env = $env;
		}
		
		public function getUrl($item) {
			return $this->env->getServiceUrl("preview", array($item->id(), "info"));
		}
		
		public function getPreview($item) {
			$dataUrl = $this->env->getServiceUrl("preview", array($item->id(), "content"), TRUE);
			return array(
				"html" => '<div id="file-preview-container" style="overflow:auto; max-height:300px"><img src="'.$dataUrl.'" style="max-width:400px"></div>'
			);
		}
	}
?>