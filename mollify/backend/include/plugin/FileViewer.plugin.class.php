<?php

	/**
	 * Copyright (c) 2008- Samuli J�rvel�
	 *
	 * All rights reserved. This program and the accompanying materials
	 * are made available under the terms of the Eclipse Public License v1.0
	 * which accompanies this distribution, and is available at
	 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
	 * this entire header must remain intact.
	 */


	class FileViewer extends PluginBase {
		const ID = "file_viewer";
		
		public function __construct($env) {
			parent::__construct($env, self::ID);
		}
		
		public function setup() {
			$preview = $this->env->features()->isFeatureEnabled('file_preview');
			$view = $this->env->features()->isFeatureEnabled('file_view');
			
			if ($preview or $view) {
				if ($view)
					$this->addService("view", "FilePreviewServices");
				if ($preview)
					$this->addService("preview", "FilePreviewServices");
				require_once("file_viewer/FilePreviewController.class.php");
				
				$controller = new FilePreviewController($this->env, $view, $preview);
				$this->env->registerObject("preview", $controller);
				$this->env->filesystem()->registerProvider($controller);
			}
		}
		
		public function __toString() {
			return "FileViewerPlugin";
		}
	}
?>