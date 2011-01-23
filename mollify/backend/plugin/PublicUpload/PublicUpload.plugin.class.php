<?php
	class PublicUpload extends PluginBase {
		public function setup() {
			$this->addService("public_upload", "PublicUploadServices");
		}
		
		public function getClientPlugin() {
			return "client/plugin.js";
		}
		
		public function __toString() {
			return "PublicUploadPlugin";
		}
	}
?>