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
			"zip_options" => array(),
			"permission_file" => "mollify.uac",
			"description_file" => "mollify.dsc",
			"debug" => FALSE
		);
		
		function __construct($settings) {
			if (isset($settings) and $settings != NULL) {
				foreach(self::$VALUES as $s=>$v) {
					if (!array_key_exists($s, $settings)) $this->settings[$s] = $v;
					else $this->settings[$s] = $settings[$s];
				}
			}
		}

		public function setting($setting) {
			return $this->settings[$setting];
		}
		
		public function hasSetting($setting) {
			return array_key_exists($setting, $this->settings);
		}
		
		function log() {
			Logging::logDebug("SETTINGS: ".Util::array2str($this->settings));
		}
	}
?>