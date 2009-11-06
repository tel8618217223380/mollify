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
	 
	$trace = array();

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
	
	function get_session_name() {
		global $SETTINGS;
		
		if (isset($SETTINGS["session_name"])) return "MOLLIFY_SESSION_".$SETTINGS["session_name"];
		return "MOLLIFY_SESSION";
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
			"max_upload_total_size" => in_bytes(ini_get("post_max_size")),
			"allowed_file_upload_types" => get_allowed_file_upload_types()
		);
	}
	
	function get_allowed_file_upload_types() {
		global $SETTINGS;
		if (!isset($SETTINGS['allowed_file_upload_types'])) return array();
		
		$types = array();
		foreach($SETTINGS['allowed_file_upload_types'] as $type) {
			$pos = strrpos($type, ".");
			if ($pos === FALSE) $types[] = $type;
			else $types[] = substr($type, $pos+1);
		}
		return $types;
	}

	function get_features() {
		return array(
			"file_upload" => get_setting("enable_file_upload", TRUE),
			"folder_actions" => get_setting("enable_folder_actions", TRUE),
			"file_upload_progress" => get_setting("enable_file_upload_progress", FALSE),
			"zip_download" => get_setting("enable_zip_download", FALSE),
			"change_password" => get_setting("enable_change_password", TRUE),
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

	function log_debug($message) {
		global $trace;
		if (!is_debug()) return;
		$trace[] = $message;
		error_log("MOLLIFY DEBUG: ".$message);
		FB::log($message);
	}
	
	function log_message($message) {
		global $trace;
		error_log("MOLLIFY: ".$message);
		if (is_debug()) {
			$trace[] = $message;
			FB::log($message);
		}
	}
	
	function log_error($message) {
		global $trace;
		error_log("MOLLIFY ERROR: ".$message);
		if (is_debug()) {
			$trace[] = $message;
			FB::error($message);
		}
	}
	
	function log_request() {
		if (!is_debug()) return;
		log_debug("REQUEST=".array_to_str($_SERVER).", POST=".array_to_str($_POST).", GET=".array_to_str($_GET));
	}
	
	function array_to_str($a) {
		$r = "{";
		$first = TRUE;
		foreach($a as $k=>$v) {
			if (!$first) $r .= ", ";
			
			$val = $v;
			if (is_array($v)) $val = array_to_str($v);
			
			$r .= $k.':'.$val;
			$first = FALSE;
		}
		return $r."}";
	}
		
	function get_trace() {
		global $trace;
		return $trace;
	}
	
	function is_debug() {
		global $SETTINGS;
		return $SETTINGS['debug'];
	}
?>