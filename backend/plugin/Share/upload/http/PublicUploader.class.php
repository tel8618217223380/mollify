<?php
	/**
	 * PublicUploader.class.php
	 *
	 * Copyright 2008- Samuli Järvelä
	 * Released under GPL License.
	 *
	 * License: http://www.mollify.org/license.php
	 */

	class PublicUploader {
		private $env;
		
		public function __construct($env) {
			$this->env = $env;
		}
		
		public function showPage($shareId, $folder) {
			$this->show($shareId, $folder, "upload.php");
		}
		
		public function uploadTo($shareId, $folder) {
			$this->env->filesystem()->uploadTo($folder);
			$this->show($shareId, $folder, "success.php");
		}
		
		private function show($shareId, $folder, $page) {
			global $UPLOAD_URL, $RESOURCE_URL, $FOLDER_NAME;
			$UPLOAD_URL = $this->env->getServiceUrl("public", array($shareId), TRUE);
			$RESOURCE_URL = $this->env->getPluginUrl("Share", "upload/http");
			$FOLDER_NAME = $folder->name();
			include($page);
		}
	}
?>