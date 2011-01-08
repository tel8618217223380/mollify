<?php
	class GuestUpload extends PluginBase {
		public function setup() {
			//$this->addService("share", "ShareServices");
		}
		
		public function getClientPlugin() {
			return "client/plugin.js";
		}
		
		public function __toString() {
			return "GuestUploadPlugin";
		}
	}
?>