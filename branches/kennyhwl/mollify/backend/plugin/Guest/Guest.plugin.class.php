<?php
	class Guest extends PluginBase {
		public function setup() {
			$this->addService("guest", "GuestServices");
		}
		
		public function getClientPlugin() {
			return "client/plugin.js";
		}
		
		public function __toString() {
			return "GuestPlugin";
		}
	}
?>