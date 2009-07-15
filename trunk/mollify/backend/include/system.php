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

	function in_bytes($amount) {
	    $amount = trim($amount);
	    $last = strtolower($amount[strlen($amount)-1]);
	    
	    switch ($last) {
	        case 'g':
	            $amount *= 1024;
	        case 'm':
	            $amount *= 1024;
	        case 'k':
	            $amount *= 1024;
	    }
	
	    return (int)$amount;
	}

	function get_filesystem_id($root_id, $path = "") {
		if (strlen($path) > 0) {
			$root_path = get_root_path($root_id);
			$path = substr($path, strlen($root_path));
		}
		return base64_encode($root_id.':'.DIRECTORY_SEPARATOR.$path);
	}

	function import_configuration_provider() {
		global $CONFIGURATION_PROVIDER;
		
		if (isset($CONFIGURATION_PROVIDER)) {
			$provider = trim(strtolower($CONFIGURATION_PROVIDER));
			
			if ($provider === 'file') {
				require_once "configuration_provider_file.php";
			} else if ($provider === 'mysql') {
				require_once "mysql/configuration_provider_mysql.php";
			} else {
				log_error("Unsupported data provider: ".$CONFIGURATION_PROVIDER);
				die("Unsupported data provider: ".$CONFIGURATION_PROVIDER);
			}
		} else {
			require_once "configuration_provider_file.php";
		}
		
		init_configuration_provider();
	}
	
	function get_configuration_setting($name) {
		$settings = $_SESSION["configuration_settings"];
		if (array_key_exists($name, $settings)) return $settings[$name];
		return FALSE;
	}
	
	function get_setting($setting_name, $default) {
		global $SETTINGS;
		if (!isset($SETTINGS) or !isset($SETTINGS[$setting_name])) return $default;
		return $SETTINGS[$setting_name];
	}

	function get_filesystem_session_info() {
		return array(
			"max_upload_file_size" => in_bytes(ini_get("upload_max_filesize")),
			"max_upload_total_size" => in_bytes(ini_get("post_max_size"))
		);
	}

	function get_features() {
		return array(
			"file_upload" => get_setting("enable_file_upload", TRUE),
			"folder_actions" => get_setting("enable_folder_actions", TRUE),
			"file_upload_progress" => get_setting("enable_file_upload_progress", FALSE),
			"zip_download" => get_setting("enable_zip_download", FALSE),
			"description_update" => get_configuration_setting("description_update"),
			"permission_update" => get_configuration_setting("permission_update"),
			"configuration_update" => get_configuration_setting("configuration_update")
		);
	}
	
	function initialize_logging() {
		global $SETTINGS;
		if (!isset($SETTINGS['debug']) or !$SETTINGS['debug']) {
			$SETTINGS['debug'] = FALSE;
			return;
		}
		require_once('FirePHPCore/fb.php');
		FB::setEnabled(true);
	}
	
	function log_message($message) {
		global $SETTINGS;
		if ($SETTINGS['debug']) FB::log($message);
	}
	
	function log_error($message) {
		global $SETTINGS;
		error_log("MOLLIFY: ".$message);
		if ($SETTINGS['debug']) FB::error($message);
	}
?>