<?php
	class GuestUpload extends PluginBase {
		public function setup() {
			$this->addService("guestupload", "GuestUploadServices");
		}
		
		public function getClientPlugin() {
			return "client/plugin.js";
		}
		
		public function __toString() {
			return "GuestUploadPlugin";
		}
	}
?>