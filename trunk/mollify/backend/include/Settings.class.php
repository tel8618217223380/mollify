<?php
	class Settings {
		private $settings = array();
		
		private static $VALUES = array(
			"session_name" => NULL,
			"enable_file_upload" => TRUE,
			"enable_folder_actions" => TRUE,
			"enable_file_upload_progress" => FALSE,
			"enable_zip_download" => FALSE,
			"enable_change_password" => TRUE,
			"enable_description_update" => FALSE,
			"enable_permission_update" => FALSE,
			"allowed_file_upload_types" => array(),
			"firebug_logging" => FALSE,
			"debug" => FALSE
		);
		
		function __construct($settings) {
			foreach(self::$VALUES as $s=>$v) {
				if (!array_key_exists($s, $settings)) $this->settings[$s] = $v;
				else $this->settings[$s] = $settings[$s];
			}
		}
				
		public function getSetting($setting) {
			return $this->settings[$setting];
		}
		
		function log() {
			Logging::logDebug("SETTINGS: ".Util::array2str($this->settings));
		}
	}
?>