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

	class FileViewer extends PluginBase {
		public function setup() {
			$preview = $this->getSetting('enable_file_preview', TRUE);
			$view = $this->getSetting('enable_file_view', TRUE);
			if (!$preview and !$view) return;
			
			if ($view)
				$this->addService("view", "FileViewerServices");
			if ($preview)
				$this->addService("preview", "FileViewerServices");
			
			require_once("FileViewerController.class.php");
			
			$controller = new FileViewerController($this, $view, $preview);
			$this->env->registerObject("file_viewer", $controller);
			$this->env->filesystem()->registerDetailsPlugin($controller);
		}
		
		public function __toString() {
			return "FileViewerPlugin";
		}
	}
?>